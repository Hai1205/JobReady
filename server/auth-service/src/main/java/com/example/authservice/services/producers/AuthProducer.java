package com.example.authservice.services.producers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.authservice.dtos.UserDto;
import com.example.authservice.dtos.responses.RPCResponse;

public class AuthProducer {
    private final RabbitTemplate rabbitTemplate;

    public AuthProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RPCResponse<UserDto> findUserByEmail(String email) {
        String exchange = "user.profile.exchange";
        String routingKey = "user.profile.find.by.email.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<UserDto> createUser(String username, String email, String password, String fullname) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.create.request";

        Map<String, Object> message = new HashMap<>();
        message.put("username", username);
        message.put("email", email);
        message.put("password", password);
        message.put("fullname", fullname);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<UserDto> activateUser(String email) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.activate.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<UserDto> changePasswordUser(String email, String currentPassword, String newPassword) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.change.password.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);
        message.put("currentPassword", currentPassword);
        message.put("newPassword", newPassword);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<UserDto> forgotPasswordUser(String email, String newPassword) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.forgot.password.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);
        message.put("newPassword", newPassword);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<UserDto> authenticateUser(String email, String currentPassword) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.authenticate.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);
        message.put("currentPassword", currentPassword);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

    public RPCResponse<String> resetPasswordUser(String email) {
        String exchange = "auth.user.exchange";
        String routingKey = "auth.user.reset.password.request";

        Map<String, Object> message = new HashMap<>();
        message.put("email", email);

        return (RPCResponse<String>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }
}
