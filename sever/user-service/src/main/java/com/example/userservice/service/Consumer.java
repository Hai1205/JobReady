package com.example.userservice.service;

import com.example.userservice.config.rabbitmq.BaseConsumer;
import com.example.userservice.config.rabbitmq.RabbitConfig;
import com.example.userservice.dto.RabbitHeader;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.response.RabbitResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Consumer extends BaseConsumer {

    private final UserService userService;

    @RabbitListener(queues = "user.find.by.email.queue")
    public void handleFindByEmail(org.springframework.amqp.core.Message message) {
        RabbitHeader header = extractHeader(message);

        try {
            String email = extractPayload(message, String.class);
            UserDto user = userService.findByEmail(email);

            var response = RabbitResponse.<UserDto>builder()
                    .code(200)
                    .message("Success")
                    .data(user)
                    .build();

            rpcService.sendReply(RabbitConfig.USER_EXCHANGE,
                    header.getReplyTo(),
                    header.getCorrelationId(),
                    response);
        } catch (Exception e) {
            rpcService.sendReply(RabbitConfig.USER_EXCHANGE,
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
