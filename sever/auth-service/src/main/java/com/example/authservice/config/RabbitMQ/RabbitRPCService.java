package com.example.authservice.config.RabbitMQ;

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
                        .build();
            }

            var wrapper = Map.of(
                    "header", header,
                    "payload", payload
            );

            String json = objectMapper.writeValueAsString(wrapper);
            Message message = new Message(json.getBytes(), new MessageProperties());

            rabbitTemplate.send(exchange, routingKey, message);

            Object response = rabbitTemplate.receiveAndConvert(header.getReplyTo(), 8000);
            if (response == null)
                throw new RuntimeException("Timeout waiting for response");

            return objectMapper.convertValue(objectMapper.readTree(response.toString()).get("payload"), responseType);

        } catch (Exception e) {
            throw new RuntimeException("❌ [RabbitRPCService] " + e.getMessage(), e);
        }
    }

    /**
     * Trả kết quả phản hồi
     */
    public void sendReply(String exchange, String replyTo, String correlationId, Object payload) {
        try {
            var header = RabbitHeader.builder()
                    .correlationId(correlationId)
                    .timestamp(System.currentTimeMillis())
                    .build();

            var wrapper = Map.of(
                    "header", header,
                    "payload", payload
            );

            String json = objectMapper.writeValueAsString(wrapper);
            rabbitTemplate.convertAndSend(exchange, replyTo, json);

            System.out.println("✅ [Reply Sent] corrId=" + correlationId);

        } catch (Exception e) {
            System.err.println("❌ [Reply Error] " + e.getMessage());
        }
    }
}

