package com.example.rabbitmq.services;

import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.dtos.RabbitResponse;
import com.example.rabbitmq.exceptions.RemoteRpcException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Improved RabbitMQ RPC Service với Direct Reply-To Pattern
 * 
 * Key Improvements:
 * 1. Non-blocking với CompletableFuture
 * 2. Direct Reply-To thay vì shared queue
 * 3. Proper correlation ID matching
 * 4. Automatic timeout và cleanup
 * 5. Thread-safe concurrent request handling
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitRPCService {

    public final RabbitTemplate rabbitTemplate;
    public final ObjectMapper objectMapper;

    // Default timeout - increased for better reliability
    public static final long DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Initialize Direct Reply-to listener after bean construction
     */
    @PostConstruct
    public void init() {
        // Configure RabbitTemplate for Direct Reply-to
        rabbitTemplate.setReplyAddress(Address.AMQ_RABBITMQ_REPLY_TO);
        rabbitTemplate.setReplyTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);

        // Additional connection configuration for reliability
        rabbitTemplate.setUseDirectReplyToContainer(true);

        log.info("✅ RabbitRPCService initialized with Direct Reply-to (replyAddress: {}, timeout: {}s)",
                Address.AMQ_RABBITMQ_REPLY_TO, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Non-blocking RPC call với CompletableFuture
     * SIMPLIFIED: Dùng Spring AMQP's convertSendAndReceive() thay vì manual
     * tracking
     */
    public <R> CompletableFuture<R> sendAndReceiveAsync(
            String exchange,
            String routingKey,
            RabbitHeader header,
            Object payload,
            Class<R> responseType) {

        return sendAndReceiveAsync(exchange, routingKey, header, payload, responseType, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Non-blocking RPC call với custom timeout
     * USES Spring AMQP's built-in Direct Reply-to support
     */
    public <R> CompletableFuture<R> sendAndReceiveAsync(
            String exchange,
            String routingKey,
            RabbitHeader header,
            Object payload,
            Class<R> responseType,
            long timeoutSeconds) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Generate correlation ID
                final String correlationId = UUID.randomUUID().toString();

                // Build header
                final RabbitHeader finalHeader;
                if (header == null) {
                    finalHeader = RabbitHeader.builder()
                            .correlationId(correlationId)
                            .timestamp(System.currentTimeMillis())
                            .build();
                } else {
                    header.setCorrelationId(correlationId);
                    finalHeader = header;
                }

                // Create message wrapper
                Map<String, Object> wrapper = Map.of(
                        "header", finalHeader,
                        "payload", payload);

                String json = objectMapper.writeValueAsString(wrapper);

                log.debug("📤 RPC Request - correlationId: {}, routing: {}.{}", correlationId, exchange, routingKey);

                // Create message manually to avoid double serialization
                Message requestMessage = MessageBuilder
                        .withBody(json.getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setCorrelationId(correlationId)
                        .setReplyTo(Address.AMQ_RABBITMQ_REPLY_TO)
                        .build();

                // Use sendAndReceive instead of convertSendAndReceive to avoid automatic
                // conversion
                Message responseMessage = rabbitTemplate.sendAndReceive(exchange, routingKey, requestMessage);

                if (responseMessage == null) {
                    throw new TimeoutException(
                            "RPC timeout after " + timeoutSeconds + "s for correlationId: " + correlationId);
                }

                Object response = new String(responseMessage.getBody(), StandardCharsets.UTF_8);

                // Parse response
                String responseJson;
                if (response instanceof String) {
                    responseJson = (String) response;
                } else if (response instanceof byte[]) {
                    responseJson = new String((byte[]) response, StandardCharsets.UTF_8);
                } else {
                    // If it's already an object, convert to JSON string
                    responseJson = objectMapper.writeValueAsString(response);
                }

                log.debug("📥 RPC Response received - correlationId: {}, length: {}", correlationId,
                        responseJson.length());

                Map<String, Object> responseWrapper = objectMapper.readValue(responseJson, Map.class);
                Object responsePayload = responseWrapper.get("payload");

                // Check if it's a RabbitResponse with error
                if (responsePayload instanceof Map) {
                    Map<String, Object> payloadMap = (Map<String, Object>) responsePayload;
                    Object code = payloadMap.get("code");
                    if (code != null && !code.equals(200)) {
                        String errorMsg = (String) payloadMap.getOrDefault("message", "Unknown error");
                        // Attempt to parse code as int
                        int codeInt = 500;
                        try {
                            if (code instanceof Number) {
                                codeInt = ((Number) code).intValue();
                            } else {
                                codeInt = Integer.parseInt(code.toString());
                            }
                        } catch (Exception ex) {
                            // fallback
                        }
                        throw new RemoteRpcException(errorMsg, codeInt);
                    }
                    Object data = payloadMap.get("data");
                    return objectMapper.convertValue(data, responseType);
                }

                // Direct payload
                return objectMapper.convertValue(responsePayload, responseType);

            } catch (Exception e) {
                log.error("❌ RPC call failed: {}", e.getMessage());
                throw new RuntimeException("RPC call failed", e);
            }
        });
    }

    /**
     * Synchronous wrapper với retry logic cho backward compatibility
     * 
     * @param exchange     Exchange name
     * @param routingKey   Routing key
     * @param header       RabbitHeader
     * @param payload      Request payload
     * @param responseType Response class type
     * @param <R>          Response type
     * @return Response data
     */
    public <R> R sendAndReceive(
            String exchange,
            String routingKey,
            RabbitHeader header,
            Object payload,
            Class<R> responseType) {

        int maxRetries = 3;
        long retryDelayMs = 1000; // 1 second

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.debug("🔄 RPC attempt {}/{} for routing: {}.{}", attempt, maxRetries, exchange, routingKey);
                return sendAndReceiveAsync(exchange, routingKey, header, payload, responseType)
                        .get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.warn("⏰ RPC timeout on attempt {}/{}: {}", attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException("RPC timeout after " + maxRetries + " attempts", e);
                }
                try {
                    Thread.sleep(retryDelayMs * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("RPC interrupted", ie);
                }
            } catch (Exception e) {
                log.error("❌ RPC call failed on attempt {}/{}: {}", attempt, maxRetries, e.getMessage(), e);
                if (attempt == maxRetries) {
                    throw new RuntimeException("RPC call failed after " + maxRetries + " attempts: " + e.getMessage(),
                            e);
                }
                try {
                    Thread.sleep(retryDelayMs * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("RPC interrupted", ie);
                }
            }
        }

        throw new RuntimeException("RPC failed after " + maxRetries + " attempts");
    }

    /**
     * Xử lý reply từ Direct Reply-To queue
     * 
     * NOTE: KHÔNG DÙNG @RabbitListener cho Direct Reply-to!
     * Spring AMQP tự động tạo temporary consumer thông qua
     * DirectReplyToMessageListenerContainer
     * 
     * /**
     * Send reply cho RPC request (dùng trong Consumer)
     * 
     * @param replyTo       Reply queue/address
     * @param correlationId Correlation ID
     * @param payload       Response payload
     */
    public void sendReply(String replyTo, String correlationId, Object payload) {
        try {
            RabbitHeader header = RabbitHeader.builder()
                    .correlationId(correlationId)
                    .status("SUCCESS")
                    .timestamp(System.currentTimeMillis())
                    .build();

            Map<String, Object> wrapper = Map.of(
                    "header", header,
                    "payload", payload);

            String json = objectMapper.writeValueAsString(wrapper);

            Message message = MessageBuilder
                    .withBody(json.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setCorrelationId(correlationId)
                    .build();

            // Send directly to reply address
            rabbitTemplate.send(replyTo, message);

            log.debug("📤 Reply sent - correlationId: {}", correlationId);

        } catch (Exception e) {
            log.error("❌ Failed to send reply for correlationId: {}", correlationId, e);
        }
    }

    /**
     * Helper: Send success reply (using replyTo from RabbitMQ Message Properties)
     */
    public void sendSuccessReply(Message message, RabbitHeader header, RabbitResponse<?> response) {
        // Use replyTo and correlationId from RabbitMQ MessageProperties (Direct
        // Reply-To pattern)
        String replyTo = message.getMessageProperties().getReplyTo();
        String correlationId = message.getMessageProperties().getCorrelationId();

        log.debug("📨 Sending success reply to: {}, correlationId: {}", replyTo, correlationId);
        sendReply(replyTo, correlationId, response);
    }

    /**
     * Helper: Send error reply (using replyTo from RabbitMQ Message Properties)
     */
    public void sendErrorReply(Message message, RabbitHeader header, String errorMessage) {
        var errorResponse = RabbitResponse.builder()
                .code(500)
                .message(errorMessage)
                .data(null)
                .build();

        // Use replyTo and correlationId from RabbitMQ MessageProperties (Direct
        // Reply-To pattern)
        String replyTo = message.getMessageProperties().getReplyTo();
        String correlationId = message.getMessageProperties().getCorrelationId();

        log.debug("📨 Sending error reply to: {}, correlationId: {}", replyTo, correlationId);
        sendReply(replyTo, correlationId, errorResponse);
    }

    /**
     * @deprecated Use sendSuccessReply(Message, RabbitHeader, RabbitResponse)
     *             instead
     */
    @Deprecated
    public void sendSuccessReply(RabbitHeader header, RabbitResponse<?> response) {
        String replyTo = header.getReplyTo();
        String correlationId = header.getCorrelationId();

        sendReply(replyTo, correlationId, response);
    }

    /**
     * @deprecated Use sendErrorReply(Message, RabbitHeader, String) instead
     */
    @Deprecated
    public void sendErrorReply(RabbitHeader header, String errorMessage) {
        var errorResponse = RabbitResponse.builder()
                .code(500)
                .message(errorMessage)
                .data(null)
                .build();

        String replyTo = header.getReplyTo();
        String correlationId = header.getCorrelationId();

        sendReply(replyTo, correlationId, errorResponse);
    }

    /**
     * Helper: Send cached reply
     */
    public void sendCachedReply(RabbitHeader header, String cachedJson) {
        try {
            RabbitResponse<?> response = objectMapper.readValue(cachedJson, RabbitResponse.class);
            sendSuccessReply(header, response);
        } catch (Exception e) {
            log.error("❌ Error sending cached reply", e);
            sendErrorReply(header, "Failed to process cached result");
        }
    }

    /**
     * Extract payload from RabbitMQ message
     * 
     * @param message Message from RabbitMQ
     * @param clazz   Class of payload
     * @return Payload object
     * @param <T> Type of payload
     */
    public <T> T extractPayload(Message message, TypeReference<T> typeRef) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("📩 Extracting payload from message body (length={})", body.length());

            JsonNode root = objectMapper.readTree(body);

            // Handle double-wrapped JSON (string containing JSON)
            if (root.isTextual()) {
                log.debug("� Detected double-wrapped JSON in payload, parsing inner JSON...");
                root = objectMapper.readTree(root.asText());
            }

            JsonNode payloadNode = root.get("payload");

            if (payloadNode == null) {
                log.error("❌ No 'payload' field found in message body");
                log.error("📄 Root node type: {}", root.getNodeType());
                log.error("📄 Full message body: {}", body);
                throw new RuntimeException("No 'payload' field found in message");
            }

            T payload = objectMapper.readValue(payloadNode.toString(), typeRef);
            return payload;
        } catch (Exception e) {
            log.error("❌ Error extracting payload: {}", e.getMessage(), e);
            throw new RuntimeException("❌ [BaseConsumer] Error extracting payload: " + e.getMessage(), e);
        }
    }

    /**
     * Extract header from RabbitMQ message
     * 
     * @param message Message from RabbitMQ
     * @return RabbitHeader object
     */
    public RabbitHeader extractHeader(Message message) {
        try {
            String body = new String(message.getBody());
            log.debug("📩 Extracting header from message body (length={})", body.length());

            JsonNode root = objectMapper.readTree(body);

            // Check if root is a String (double-wrapped JSON)
            if (root.isTextual()) {
                log.debug("� Detected double-wrapped JSON, parsing again...");
                String innerJson = root.asText();
                root = objectMapper.readTree(innerJson);
            }

            JsonNode headerNode = root.get("header");

            if (headerNode == null) {
                log.error("❌ No 'header' field found in message body");
                log.error("📄 Root node type: {}", root.getNodeType());
                log.error("📄 Full message body: {}", body);
                throw new RuntimeException("No 'header' field found in message");
            }

            RabbitHeader header = objectMapper.treeToValue(headerNode, RabbitHeader.class);
            log.debug("✅ Header extracted: correlationId={}", header != null ? header.getCorrelationId() : "null");

            return header;
        } catch (Exception e) {
            log.error("❌ Error extracting header: {}", e.getMessage(), e);
            throw new RuntimeException("❌ [BaseConsumer] Error extracting header: " + e.getMessage(), e);
        }
    }

    public RabbitHeader generateHeader(String replyTo, String replyExchange, String sourceService,
            String targetService) {
        return RabbitHeader.builder()
                .correlationId(UUID.randomUUID().toString())
                .replyTo(replyTo)
                .replyExchange(replyExchange)
                .timestamp(System.currentTimeMillis())
                .sourceService(sourceService)
                .targetService(targetService)
                .build();
    }
}
