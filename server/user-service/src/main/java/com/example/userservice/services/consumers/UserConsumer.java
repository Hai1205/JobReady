package com.example.userservice.services.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.services.apis.UserService;
import com.example.rabbitcommon.dtos.RPCResponse;

import java.util.Map;
import java.util.UUID;

@Component
public class UserConsumer {

    @Autowired
    private UserService userService;

    // -------------------------------
    // USER PROFILE (exchange: user.profile.exchange)
    // -------------------------------

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "user.profile.find-by-id.user-service.queue", durable = "true"), exchange = @Exchange(value = "user.profile.exchange", type = "topic"), key = "user.profile-find-by-id.request"))
    public RPCResponse<UserDto> handleFindUserById(@Payload Map<String, Object> message) {
        try {
            UUID userId = UUID.fromString((String) message.get("userId"));
            UserDto userData = userService.handleFindById(userId);
            if (userData != null) {
                return new RPCResponse<>(200, "User found successfully", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error finding user by ID: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "user.profile.find-by-email.user-service.queue", durable = "true"), exchange = @Exchange(value = "user.profile.exchange", type = "topic"), key = "user.profile.find-by-email.request"))
    public RPCResponse<UserDto> handleFindUserByEmail(@Payload Map<String, Object> message) {
        try {
            String email = (String) message.get("email");
            UserDto userData = userService.handleFindByEmail(email);
            if (userData != null) {
                return new RPCResponse<>(200, "User found successfully", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error finding user by email: " + e.getMessage(), null);
        }
    }
    
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "user.profile.find-by-identifier.user-service.queue", durable = "true"), exchange = @Exchange(value = "user.profile.exchange", type = "topic"), key = "user.profile.find-by-identifier.request"))
    public RPCResponse<UserDto> handleFindUserByIdentifier(@Payload Map<String, Object> message) {
        try {
            String identifier = (String) message.get("identifier");
            UserDto userData = userService.handleFindByIdentifier(identifier);
            if (userData != null) {
                return new RPCResponse<>(200, "User found successfully", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error finding user by email: " + e.getMessage(), null);
        }
    }

    // -------------------------------
    // AUTH USER (exchange: auth.user.exchange)
    // -------------------------------

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.create.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.create.request"))
    public RPCResponse<UserDto> handleCreateUser(@Payload Map<String, Object> message) {
        try {
            String username = (String) message.get("username");
            String email = (String) message.get("email");
            String password = (String) message.get("password");
            String fullname = (String) message.get("fullname");

            UserDto userData = userService.handleCreateUser(username, email, password, fullname, "user", "pending",
                    null);
            if (userData != null) {
                return new RPCResponse<>(200, "User created successfully", userData);
            }
            return new RPCResponse<>(400, "User creation failed", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error creating user: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.activate.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.activate.request"))
    public RPCResponse<UserDto> handleActivateUser(@Payload Map<String, Object> message) {
        try {
            String email = (String) message.get("email");
            UserDto userData = userService.handleActivateUser(email);
            if (userData != null) {
                return new RPCResponse<>(200, "User activated successfully", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error activating user: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.authenticate.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.authenticate.request"))
    public RPCResponse<UserDto> handleAuthenticateUser(@Payload Map<String, Object> message) {
        try {
            String identifier = (String) message.get("identifier");
            String currentPassword = (String) message.get("currentPassword");

            UserDto userData = userService.handleAuthenticateUser(identifier, currentPassword);
            if (userData != null) {
                return new RPCResponse<>(200, "Authentication successful", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error authenticating user: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.change-password.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.change-password.request"))
    public RPCResponse<UserDto> handleChangePasswordUser(@Payload Map<String, Object> message) {
        try {
            String email = (String) message.get("email");
            String currentPassword = (String) message.get("currentPassword");
            String newPassword = (String) message.get("newPassword");

            UserDto userData = userService.handleChangePasswordUser(email, currentPassword, newPassword);
            if (userData != null) {
                return new RPCResponse<>(200, "Password changed successfully", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error changing password: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.forgot-password.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.forgot-password.request"))
    public RPCResponse<UserDto> handleForgotPasswordUser(@Payload Map<String, Object> message) {
        try {
            String email = (String) message.get("email");
            String newPassword = (String) message.get("newPassword");

            UserDto userData = userService.handleForgotPasswordUser(email, newPassword);
            if (userData != null) {
                return new RPCResponse<>(200, "Password reset successful", userData);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error resetting password: " + e.getMessage(), null);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "auth.user.reset-password.user-service.queue", durable = "true"), exchange = @Exchange(value = "auth.user.exchange", type = "topic"), key = "auth.user.reset-password.request"))
    public RPCResponse<String> handleResetPasswordUser(@Payload Map<String, Object> message) {
        try {
            String email = (String) message.get("email");
            String newPassword = userService.handleResetPasswordUser(email);
            if (newPassword != null) {
                return new RPCResponse<>(200, "Password reset successful", newPassword);
            }
            return new RPCResponse<>(404, "User not found", null);
        } catch (Exception e) {
            return new RPCResponse<>(500, "Error resetting password: " + e.getMessage(), null);
        }
    }
}
