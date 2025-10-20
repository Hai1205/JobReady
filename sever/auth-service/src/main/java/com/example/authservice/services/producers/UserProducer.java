package com.example.authservice.services.producers;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.authservice.dtos.UserDto;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.services.RabbitRPCService;

@Service
@RequiredArgsConstructor
public class UserProducer {
        private final RabbitRPCService rpcService;

        public UserDto findUserByEmail(String email) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);

                System.out.println("Sending RabbitMQ request to find user by email: " + email);
                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_FIND_BY_EMAIL,
                                header,
                                params,
                                UserDto.class);

                System.out.println("Received RabbitMQ response for user: "
                                + (userDto != null ? userDto.toString() : "null"));

                return userDto;
        }

        public UserDto createUser(String username, String email, String password, String fullname) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("fullname", fullname);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_CREATE,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }
        
        public UserDto activateUser(String email) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_ACTIVATE,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }

        public UserDto changePasswordUser(String email, String currentPassword, String newPassword) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("currentPassword", currentPassword);
                params.put("newPassword", newPassword);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_CHANGE_PASSWORD,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }

        public UserDto forgotPasswordUser(String email, String newPassword) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("newPassword", newPassword);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_FORGOT_PASSWORD,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }

        public UserDto authenticateUser(String email, String currentPassword) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("currentPassword", currentPassword);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_AUTHENTICATE,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }

        public String resetPasswordUser(String email) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);

                String password = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_RESET_PASSWORD,
                                header,
                                params,
                                String.class);

                return password;
        }
}
