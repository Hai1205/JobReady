package com.example.userservice.service.consumers;

import com.example.rabbitmq.consumer.BaseConsumer;
import com.example.rabbitmq.dto.RabbitHeader;
import com.example.rabbitmq.dto.RabbitResponse;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer extends BaseConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EXCHANGE = "user.exchange";

    @RabbitListener(queues = "user.find.by.email.queue")
    public void handleFindByEmail(org.springframework.amqp.core.Message message) {
        RabbitHeader header = extractHeader(message);

        try {
            Map<String, Object> params = objectMapper.readValue(message.getBody(), new TypeReference<Map<String, Object>>() {});
            String email = (String) params.get("email");
            UserDto user = userService.findByEmail(email);

            var response = RabbitResponse.<UserDto>builder()
                    .code(200)
                    .message("Success")
                    .data(user)
                    .build();

            rpcService.sendReply(USER_EXCHANGE,
                    header.getReplyTo(),
                    header.getCorrelationId(),
                    response);
        } catch (Exception e) {
            rpcService.sendReply(USER_EXCHANGE,
                    header.getReplyTo(),
                    header.getCorrelationId(),
                    RabbitResponse.builder()
                            .code(404)
                            .message("User not found: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
