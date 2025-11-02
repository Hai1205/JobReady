package com.example.userservice.services.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.dtos.response.RPCResponse;
import com.example.userservice.services.apis.UserService;

import java.util.Map;
import java.util.UUID;

@Component
public class UserConsumer {

        @Autowired
        private UserService userService;

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "user.profile.find.by.id.user-service.queue", durable = "true"), exchange = @Exchange(value = "user.profile.exchange", type = "topic"), key = "user.profile.find.by.id.request"))
        public RPCResponse<UserDto> handleFindUserById(@Payload Map<String, Object> message) {
                UUID userId = (UUID) message.get("userId");

                UserDto userData = userService.handleFindById(userId);
                if (userData != null) {
                        return new RPCResponse<UserDto>(200, "Authentication successful", userData);
                }

                return new RPCResponse<UserDto>(404, "User not found", null);
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "user.profile.find.by.email.user-service.queue", durable = "true"), exchange = @Exchange(value = "user.profile.exchange", type = "topic"), key = "user.profile.find.by.email.request"))
        public RPCResponse<UserDto> handleFindUserByEmail(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");

                        UserDto userData = userService.handleFindByEmail(email);
                        return new RPCResponse<UserDto>(200, "User found", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(404, "User not found", null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.create.request"))
        public RPCResponse<UserDto> handleCreateUser(@Payload Map<String, Object> message) {
                try {
                        String username = (String) message.get("username");
                        String email = (String) message.get("email");
                        String password = (String) message.get("password");
                        String fullname = (String) message.get("fullname");

                        UserDto userData = userService.handleCreateUser(username, email, password, fullname, "user",
                                        "active", null);
                        return new RPCResponse<UserDto>(201, "User created successfully", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(400, e.getMessage(), null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.activate.request"))
        public RPCResponse<UserDto> handleActivateUser(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");

                        UserDto userData = userService.handleActivateUser(email);
                        return new RPCResponse<UserDto>(200, "User activated successfully", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(404, e.getMessage(), null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.authenticate.request"))
        public RPCResponse<UserDto> handleAuthenticateUser(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");
                        String currentPassword = (String) message.get("currentPassword");

                        UserDto userData = userService.handleAuthenticateUser(email, currentPassword);
                        return new RPCResponse<UserDto>(200, "Authentication successful", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(401, e.getMessage(), null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.change.password.request"))
        public RPCResponse<UserDto> handleChangePasswordUser(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");
                        String currentPassword = (String) message.get("currentPassword");
                        String newPassword = (String) message.get("newPassword");

                        UserDto userData = userService.handleChangePasswordUser(email, currentPassword, newPassword);
                        return new RPCResponse<UserDto>(200, "Password changed successfully", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(400, e.getMessage(), null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.forgot.password.request"))
        public RPCResponse<UserDto> handleForgotPasswordUser(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");
                        String newPassword = (String) message.get("newPassword");

                        UserDto userData = userService.handleForgotPasswordUser(email, newPassword);
                        return new RPCResponse<UserDto>(200, "Password reset successfully", userData);
                } catch (Exception e) {
                        return new RPCResponse<UserDto>(404, e.getMessage(), null);
                }
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.reset.password.request"))
        public RPCResponse<String> handleResetPasswordUser(@Payload Map<String, Object> message) {
                try {
                        String email = (String) message.get("email");

                        String newPassword = userService.handleResetPasswordUser(email);
                        return new RPCResponse<String>(200, "Password reset successfully", newPassword);
                } catch (Exception e) {
                        return new RPCResponse<String>(404, e.getMessage(), null);
                }
        }
}
