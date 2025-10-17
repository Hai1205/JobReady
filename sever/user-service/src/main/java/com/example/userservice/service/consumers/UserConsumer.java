package com.example.userservice.service.consumers;

import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.consumer.BaseConsumer;
import com.example.rabbitmq.dto.RabbitHeader;
import com.example.rabbitmq.dto.RabbitResponse;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer extends BaseConsumer {

        private final UserService userService;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @RabbitListener(queues = RabbitConstants.USER_FIND_BY_EMAIL_QUEUE)
        public void handleFindByEmail(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");
                        UserDto user = userService.handleFindByEmail(email);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }
        
        @RabbitListener(queues = RabbitConstants.USER_FIND_BY_ID_QUEUE)
        public void handleFindById(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        UUID userId = UUID.fromString((String) payload.get("userId"));
                        UserDto user = userService.handleFindById(userId);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
        public void handleCreateUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");
                        String password = (String) payload.get("password");
                        String fullname = (String) payload.get("fullname");

                        UserDto user = userService.handleCreateUser("", email, password, fullname, "", "", null);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_ACTIVATE_QUEUE)
        public void handleActivateUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");

                        UserDto user = userService.handleActivateUser(email);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_CHANGE_PASSWORD_QUEUE)
        public void handleChangePasswordUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");
                        String currentPassword = (String) payload.get("currentPassword");
                        String newPassword = (String) payload.get("newPassword");

                        UserDto user = userService.handleChangePasswordUser(email, currentPassword, newPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_FORGOT_PASSWORD_QUEUE)
        public void handleForgotPasswordUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");
                        String newPassword = (String) payload.get("newPassword");

                        UserDto user = userService.handleForgotPasswordUser(email, newPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_AUTHENTICATE_QUEUE)
        public void handleAuthenticateUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");
                        String currentPassword = (String) payload.get("currentPassword");

                        UserDto user = userService.handleAuthenticateUser(email, currentPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_RESET_PASSWORD_QUEUE)
        public void handleResetPasswordUser(Message message) {
                RabbitHeader header = extractHeader(message);

                try {
                        Map<String, Object> params = objectMapper.readValue(message.getBody(),
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");

                        String newPassword = userService.handleResetPasswordUser(email);

                        var response = RabbitResponse.<String>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(newPassword)
                                        .build();

                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        response);
                } catch (Exception e) {
                        String replyExchange = header.getReplyExchange() != null ? header.getReplyExchange()
                                        : RabbitConstants.USER_EXCHANGE;
                        rpcService.sendReply(replyExchange,
                                        header.getReplyTo(),
                                        header.getCorrelationId(),
                                        RabbitResponse.builder()
                                                        .code(404)
                                                        .message(e.getMessage())
                                                        .data(null)
                                                        .build());
                }
        }
}
