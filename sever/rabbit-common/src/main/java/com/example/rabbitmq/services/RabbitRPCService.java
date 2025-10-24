package com.example.rabbitmq.services;

import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.dtos.RabbitResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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

    // Cache để lưu pending requests với correlationId làm key
    public final ConcurrentHashMap<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();

    // Executor cho timeout tasks
    public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // Default timeout
    public static final long DEFAULT_TIMEOUT_SECONDS = 10;

    /**
     * Non-blocking RPC call với CompletableFuture
     * 
     * @param exchange     Exchange name
     * @param routingKey   Routing key
     * @param header       RabbitHeader (nullable)
     * @param payload      Request payload
     * @param responseType Response class type
     * @param <R>          Response type
     * @return CompletableFuture<R>
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
     */
    public <R> CompletableFuture<R> sendAndReceiveAsync(
            String exchange,
            String routingKey,
            RabbitHeader header,
            Object payload,
            Class<R> responseType,
            long timeoutSeconds) {

        CompletableFuture<R> future = new CompletableFuture<>();

        try {
            // Generate unique correlation ID
            String correlationId = UUID.randomUUID().toString();

            // Build header
            if (header == null) {
                header = RabbitHeader.builder()
                        .correlationId(correlationId)
                        .timestamp(System.currentTimeMillis())
                        .sourceService("current-service")
                        .targetService("target-service")
                        .build();
            } else {
                header.setCorrelationId(correlationId);
            }

            // Store future in cache
            pendingRequests.put(correlationId, (CompletableFuture<Object>) future);

            // Schedule timeout task
            ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
                CompletableFuture<Object> removed = pendingRequests.remove(correlationId);
                if (removed != null && !removed.isDone()) {
                    log.warn("⏰ RPC timeout for correlationId: {}, routingKey: {}", correlationId, routingKey);
                    removed.completeExceptionally(
                            new TimeoutException(String.format(
                                    "RPC timeout after %ds for correlationId: %s",
                                    timeoutSeconds, correlationId)));
                }
            }, timeoutSeconds, TimeUnit.SECONDS);

            // Cancel timeout task khi future complete
            future.whenComplete((result, error) -> timeoutTask.cancel(false));

            // Create message wrapper
            Map<String, Object> wrapper = Map.of(
                    "header", header,
                    "payload", payload);
            String json = objectMapper.writeValueAsString(wrapper);

            // Build message với Direct Reply-To
            Message message = MessageBuilder
                    .withBody(json.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setCorrelationId(correlationId)
                    .setReplyTo(Address.AMQ_RABBITMQ_REPLY_TO) // ✅ Direct Reply-To magic!
                    .build();

            // Send message
            rabbitTemplate.send(exchange, routingKey, message);

            log.debug("📤 RPC Request sent - correlationId: {}, routing: {}.{}",
                    correlationId, exchange, routingKey);

        } catch (Exception e) {
            log.error("❌ Failed to send RPC request: {}", e.getMessage(), e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * Synchronous wrapper cho backward compatibility
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

        try {
            return sendAndReceiveAsync(exchange, routingKey, header, payload, responseType)
                    .get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("⏰ RPC timeout: {}", e.getMessage());
            throw new RuntimeException("RPC timeout after " + DEFAULT_TIMEOUT_SECONDS + "s", e);
        } catch (Exception e) {
            log.error("❌ RPC call failed: {}", e.getMessage(), e);
            throw new RuntimeException("RPC call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Xử lý reply từ Direct Reply-To queue
     * Spring AMQP tự động route replies về đây dựa vào correlationId
     * 
     * Note: Queue name "amq.rabbitmq.reply-to" là pseudo-queue của RabbitMQ
     */
    @RabbitListener(queues = "#{T(org.springframework.amqp.core.Address).AMQ_RABBITMQ_REPLY_TO}")
    public void handleReply(Message message) {
        String correlationId = null;
        try {
            correlationId = message.getMessageProperties().getCorrelationId();

            if (correlationId == null) {
                log.warn("⚠️ Received reply without correlationId");
                return;
            }

            // Lấy future từ cache
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);

            if (future == null) {
                log.warn("⚠️ Received reply for unknown or expired correlationId: {}", correlationId);
                return;
            }

            // Parse response
            String body = new String(message.getBody());
            Map<String, Object> wrapper = objectMapper.readValue(body, Map.class);
            Object responsePayload = wrapper.get("payload");

            // Check if it's a RabbitResponse
            if (responsePayload instanceof Map) {
                Map<String, Object> payloadMap = (Map<String, Object>) responsePayload;

                // Check for error
                Object code = payloadMap.get("code");
                if (code != null && !code.equals(200)) {
                    String errorMsg = (String) payloadMap.getOrDefault("message", "Unknown error");
                    log.warn("⚠️ RPC error response - correlationId: {}, error: {}", correlationId, errorMsg);
                    future.completeExceptionally(new RuntimeException("Remote error: " + errorMsg));
                    return;
                }

                // Extract data
                Object data = payloadMap.get("data");
                future.complete(data);
                log.debug("✅ RPC Response received - correlationId: {}", correlationId);
            } else {
                // Direct payload
                future.complete(responsePayload);
                log.debug("✅ RPC Response received - correlationId: {}", correlationId);
            }

        } catch (Exception e) {
            log.error("❌ Error handling reply for correlationId: {}", correlationId, e);

            if (correlationId != null) {
                CompletableFuture<Object> future = pendingRequests.remove(correlationId);
                if (future != null) {
                    future.completeExceptionally(e);
                }
            }
        }
    }

    /**
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
     * Get metrics về pending requests (for monitoring)
     */
    public int getPendingRequestsCount() {
        return pendingRequests.size();
    }

    /**
     * Cleanup khi shutdown
     */
    public void shutdown() {
        scheduler.shutdown();
        pendingRequests.values()
                .forEach(future -> future.completeExceptionally(new RuntimeException("Service shutting down")));
        pendingRequests.clear();
    }

    /**
     * Helper: Send success reply
     */
    public void sendSuccessReply(RabbitHeader header, RabbitResponse<?> response) {
        String replyTo = header.getReplyTo();
        String correlationId = header.getCorrelationId();

        sendReply(replyTo, correlationId, response);
    }

    /**
     * Helper: Send error reply
     */
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
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.readValue(root.get("payload").toString(), typeRef);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting payload: " + e.getMessage());
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
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.treeToValue(root.get("header"), RabbitHeader.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting header: " + e.getMessage());
        }
    }

    public RabbitHeader generateHeader(String replyTo, String replyExchange, String sourceService, String targetService) {
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
