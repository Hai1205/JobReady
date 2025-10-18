package com.example.rabbitmq.services;

import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.dtos.RabbitResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Service for RabbitMQ RPC operations
 * Handles sending messages and receiving responses
 */
@Service
@RequiredArgsConstructor
public class RabbitRPCService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Send message with metadata and receive response
     * 
     * @param <R>          Type of response data
     * @param exchange     Exchange to send message to
     * @param routingKey   Routing key for message
     * @param header       Header with metadata
     * @param payload      Payload to send
     * @param responseType Class of response data
     * @return Response data of type R
     */
    public <R> R sendAndReceive(String exchange, String routingKey, RabbitHeader header, Object payload,
            Class<R> responseType) {
        try {
            if (header == null) {
                header = RabbitHeader.builder()
                        .correlationId(UUID.randomUUID().toString())
                        .replyTo("common.reply.queue")
                        .timestamp(System.currentTimeMillis())
                        .sourceService("default-service")
                        .targetService("target-service")
                        .build();
            }

            var wrapper = Map.of("header", header, "payload", payload);
            String json = objectMapper.writeValueAsString(wrapper);
            Message message = new Message(json.getBytes(), new MessageProperties());

            rabbitTemplate.send(exchange, routingKey, message);

            Object response = rabbitTemplate.receiveAndConvert(header.getReplyTo(), 8000);
            if (response == null)
                throw new RuntimeException("Timeout waiting for response");

            // Parse the wrapper first
            Map<String, Object> responseWrapper = objectMapper.readValue(response.toString(), Map.class);
            Object responsePayload = responseWrapper.get("payload");

            // Parse to standard RabbitResponse
            RabbitResponse<R> result = objectMapper.convertValue(responsePayload,
                    objectMapper.getTypeFactory().constructParametricType(RabbitResponse.class, responseType));

            if (result.getCode() != 200)
                throw new RuntimeException("❌ Remote error: " + result.getMessage());

            return result.getData();

        } catch (Exception e) {
            throw new RuntimeException("❌ [RabbitRPCService] " + e.getMessage(), e);
        }
    }

    /**
     * Send message with parameters and receive response
     * 
     * @param <R>          Type of response data
     * @param exchange     Exchange to send message to
     * @param routingKey   Routing key for message
     * @param header       Header with metadata
     * @param params       Map of parameters to send
     * @param responseType Class of response data
     * @return Response data of type R
     */
    public <R> R sendAndReceiveWithParams(String exchange, String routingKey, RabbitHeader header,
            Map<String, Object> params, Class<R> responseType) {
        return sendAndReceive(exchange, routingKey, header, params, responseType);
    }

    /**
     * Send reply to a received message
     * 
     * @param exchange      Exchange to send reply to
     * @param replyTo       Reply queue
     * @param correlationId Correlation ID of original message
     * @param payload       Payload to send
     */
    public void sendReply(String exchange, String replyTo, String correlationId, Object payload) {
        try {
            var header = RabbitHeader.builder()
                    .correlationId(correlationId)
                    .status("SUCCESS")
                    .timestamp(System.currentTimeMillis())
                    .build();

            String json = objectMapper.writeValueAsString(
                    Map.of("header", header, "payload", payload));

            System.out.println(
                    "✅ [Sending Reply] exchange=" + exchange + ", replyTo=" + replyTo + ", corrId=" + correlationId);
            rabbitTemplate.convertAndSend(exchange, replyTo, json);
            System.out.println("✅ [Reply Sent] corrId=" + correlationId);

        } catch (Exception e) {
            System.err.println("❌ [Reply Error] " + e.getMessage());
        }
    }
}