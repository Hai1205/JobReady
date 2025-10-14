package com.example.userservice.config.rabbitmq;

import com.example.userservice.dto.RabbitHeader;
import com.example.userservice.dto.response.RabbitResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitRPCService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gửi message với metadata tùy chọn và nhận phản hồi
     */
    public <T, R> R sendAndReceive(String exchange, String routingKey, RabbitHeader header, T payload, Class<R> responseType) {
        try {
            if (header == null) {
                header = RabbitHeader.builder()
                        .correlationId(UUID.randomUUID().toString())
                        .replyTo("common.reply.queue")
                        .timestamp(System.currentTimeMillis())
                        .sourceService("auth-service")
                        .targetService("user-service")
                        .build();
            }

            var wrapper = Map.of("header", header, "payload", payload);
            String json = objectMapper.writeValueAsString(wrapper);
            Message message = new Message(json.getBytes(), new MessageProperties());

            rabbitTemplate.send(exchange, routingKey, message);

            Object response = rabbitTemplate.receiveAndConvert(header.getReplyTo(), 8000);
            if (response == null)
                throw new RuntimeException("Timeout waiting for response");

            // Parse thành RabbitResponse chuẩn
            RabbitResponse<R> result = objectMapper.readValue(response.toString(),
                    objectMapper.getTypeFactory().constructParametricType(RabbitResponse.class, responseType));

            if (result.getCode() != 200)
                throw new RuntimeException("❌ Remote error: " + result.getMessage());

            return result.getData();

        } catch (Exception e) {
            throw new RuntimeException("❌ [RabbitRPCService] " + e.getMessage(), e);
        }
    }

    public void sendReply(String exchange, String replyTo, String correlationId, Object payload) {
        try {
            var header = RabbitHeader.builder()
                    .correlationId(correlationId)
                    .status("SUCCESS")
                    .timestamp(System.currentTimeMillis())
                    .build();

            var wrapper = Map.of("header", header, "payload",
                    RabbitResponse.builder().code(200).message("OK").data(payload).build());

            String json = objectMapper.writeValueAsString(wrapper);
            rabbitTemplate.convertAndSend(exchange, replyTo, json);
            System.out.println("✅ [Reply Sent] corrId=" + correlationId);

        } catch (Exception e) {
            System.err.println("❌ [Reply Error] " + e.getMessage());
        }
    }
}
