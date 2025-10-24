package com.example.userservice.services.consumers;

import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.dtos.RabbitResponse;
import com.example.rabbitmq.services.IdempotencyService;
import com.example.rabbitmq.services.RabbitRPCService;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.services.apis.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsumer {

        @Autowired
        private RabbitRPCService rpcService;
        private final UserService userService;
        private final IdempotencyService idempotencyService;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @RabbitListener(queues = RabbitConstants.USER_FIND_BY_EMAIL_QUEUE)
        public void handleFindByEmail(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [FindByEmail] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");

                        log.debug("🔍 [FindByEmail] Looking up email: {}", email);

                        UserDto user = userService.handleFindByEmail(email);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [FindByEmail] Success - email: {}, userId: {}",
                                        email, user != null ? user.getId() : null);

                } catch (Exception e) {
                        log.error("❌ [FindByEmail] Error - correlationId: {}", correlationId, e);
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_FIND_BY_ID_QUEUE)
        public void handleFindById(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [FindById] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        UUID userId = UUID.fromString((String) payload.get("userId"));

                        log.debug("🔍 [FindById] Looking up userId: {}", userId);

                        UserDto user = userService.handleFindById(userId);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [FindById] Success - userId: {}",
                                        userId, user != null ? user.getId() : null);

                } catch (Exception e) {
                        log.error("❌ [FindById] Error - correlationId: {}", correlationId, e);
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
        public void handleCreateUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [CreateUser] Received request - correlationId: {}", correlationId);

                try {
                        // ✅ Check idempotency
                        String idempotencyKey = "user:create:" + correlationId;

                        // Check if already failed
                        Optional<String> errorMsg = idempotencyService.getErrorMessage(idempotencyKey);
                        if (errorMsg.isPresent()) {
                                log.warn("♻️ [CreateUser] Request previously failed - correlationId: {}",
                                                correlationId);
                                rpcService.sendErrorReply(header, errorMsg.get());
                                return;
                        }

                        // Check cached result
                        Optional<String> cachedResult = idempotencyService.getCachedResult(idempotencyKey);
                        if (cachedResult.isPresent()) {
                                log.info("♻️ [CreateUser] Duplicate request, returning cached result - correlationId: {}",
                                                correlationId);
                                rpcService.sendCachedReply(header, cachedResult.get());
                                return;
                        }

                        // ✅ Acquire distributed lock
                        if (!idempotencyService.isFirstRequest(idempotencyKey)) {
                                log.warn("⚠️ [CreateUser] Concurrent request detected, skipping - correlationId: {}",
                                                correlationId);
                                // Wait và retry hoặc reject
                                throw new AmqpRejectAndDontRequeueException(
                                                "Concurrent request detected, please retry");
                        }

                        // Extract payload
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");

                        String username = (String) payload.get("username");
                        String email = (String) payload.get("email");
                        String password = (String) payload.get("password");
                        String fullname = (String) payload.get("fullname");

                        log.debug("👤 [CreateUser] Creating user - email: {}, username: {}", email, username);

                        // Process
                        UserDto user = userService.handleCreateUser(username, email, password, fullname, "", "", null);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        // ✅ Cache result
                        String resultJson = objectMapper.writeValueAsString(response);
                        idempotencyService.updateResult(idempotencyKey, resultJson);

                        // Send reply
                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [CreateUser] Success - userId: {}, email: {}", user.getId(), email);

                } catch (Exception e) {
                        log.error("❌ [CreateUser] Error - correlationId: {}", correlationId, e);

                        // Mark as failed để tránh retry liên tục với cùng lỗi
                        String idempotencyKey = "user:create:" + correlationId;
                        idempotencyService.markAsFailed(idempotencyKey, e.getMessage());

                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_ACTIVATE_QUEUE)
        public void handleActivateUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [Activate] Received request - correlationId: {}", correlationId);

                try {
                        // Idempotency key
                        String idempotencyKey = "user:activate:" + correlationId;

                        // Check cached result
                        Optional<String> cachedResult = idempotencyService.getCachedResult(idempotencyKey);
                        if (cachedResult.isPresent()) {
                                log.info("♻️ [Activate] Duplicate request, returning cached result");
                                rpcService.sendCachedReply(header, cachedResult.get());
                                return;
                        }

                        // Acquire lock
                        if (!idempotencyService.isFirstRequest(idempotencyKey)) {
                                log.warn("⚠️ [Activate] Concurrent request detected");
                                throw new AmqpRejectAndDontRequeueException("Concurrent request");
                        }

                        // Extract payload
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");

                        log.debug("✓ [Activate] Activating user - email: {}", email);

                        UserDto user = userService.handleActivateUser(email);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        // Cache result
                        String resultJson = objectMapper.writeValueAsString(response);
                        idempotencyService.updateResult(idempotencyKey, resultJson);

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [Activate] Success - email: {}, userId: {}", email, user.getId());

                } catch (Exception e) {
                        log.error("❌ [Activate] Error - correlationId: {}", correlationId, e);
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_CHANGE_PASSWORD_QUEUE)
        public void handleChangePasswordUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [ChangePassword] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");

                        String email = (String) payload.get("email");
                        String currentPassword = (String) payload.get("currentPassword");
                        String newPassword = (String) payload.get("newPassword");

                        log.debug("🔑 [ChangePassword] Changing password - email: {}", email);

                        UserDto user = userService.handleChangePasswordUser(email, currentPassword, newPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [ChangePassword] Success - email: {}", email);

                } catch (Exception e) {
                        log.error("❌ [ChangePassword] Error - correlationId: {}", correlationId, e);
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_FORGOT_PASSWORD_QUEUE)
        public void handleForgotPasswordUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [ForgotPassword] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");

                        String email = (String) payload.get("email");
                        String newPassword = (String) payload.get("newPassword");

                        log.debug("🔐 [ForgotPassword] Processing forgot password - email: {}", email);

                        UserDto user = userService.handleForgotPasswordUser(email, newPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [Authenticate] Success - email: {}, userId: {}", email, user.getId());

                } catch (Exception e) {
                        log.error("❌ [Authenticate] Failed - correlationId: {}, reason: {}",
                                        correlationId, e.getMessage());
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_AUTHENTICATE_QUEUE)
        public void handleAuthenticateUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [Authenticate] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");

                        String email = (String) payload.get("email");
                        String currentPassword = (String) payload.get("currentPassword");

                        log.debug("🔐 [Authenticate] Authenticating user - email: {}", email);

                        UserDto user = userService.handleAuthenticateUser(email, currentPassword);

                        var response = RabbitResponse.<UserDto>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(user)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [Authenticate] Success - email: {}, userId: {}", email, user.getId());

                } catch (Exception e) {
                        log.error("❌ [Authenticate] Failed - correlationId: {}, reason: {}",
                                        correlationId, e.getMessage());
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitConstants.USER_RESET_PASSWORD_QUEUE)
        public void handleResetPasswordUser(Message message) {
                RabbitHeader header = rpcService.extractHeader(message);
                String correlationId = header.getCorrelationId();

                log.info("📨 [ResetPassword] Received request - correlationId: {}", correlationId);

                try {
                        Map<String, Object> params = rpcService.extractPayload(message, new TypeReference<>() {
                        });
                        Map<String, Object> payload = (Map<String, Object>) params.get("payload");
                        String email = (String) payload.get("email");

                        log.debug("🔄 [ResetPassword] Resetting password - email: {}", email);

                        String newPassword = userService.handleResetPasswordUser(email);

                        var response = RabbitResponse.<String>builder()
                                        .code(200)
                                        .message("Success")
                                        .data(newPassword)
                                        .build();

                        rpcService.sendSuccessReply(header, response);

                        log.info("✅ [ResetPassword] Success - email: {}", email);

                } catch (Exception e) {
                        log.error("❌ [ResetPassword] Error - correlationId: {}", correlationId, e);
                        rpcService.sendErrorReply(header, e.getMessage());
                }
        }
}
