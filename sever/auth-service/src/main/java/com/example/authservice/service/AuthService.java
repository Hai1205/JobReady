package com.example.authservice.service;

import com.example.authservice.config.RabbitConfig;
import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.event.UserLoginEvent;
import com.example.authservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String authenticate(AuthRequest authRequest) {
        // In a real application, you would validate credentials against a database
        // For demo purposes, we'll use simple validation
        if (isValidUser(authRequest)) {
            String token = jwtUtil.generateToken(authRequest.getUsername(), "userId");

            // Publish login event to RabbitMQ
            publishUserLoginEvent(authRequest.getUsername());

            return token;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }

    public void registerUser(RegisterRequest registerRequest) {
        // Validate registration request
        if (!isValidRegisterRequest(registerRequest)) {
            throw new RuntimeException("Invalid registration data");
        }

        // Publish registration event to RabbitMQ for user-service to create user
        publishUserRegistrationEvent(registerRequest);
    }

    private boolean isValidUser(AuthRequest authRequest) {
        // Simple validation - in real app, check against database
        return authRequest.getUsername() != null &&
                authRequest.getPassword() != null &&
                !authRequest.getUsername().isEmpty() &&
                !authRequest.getPassword().isEmpty();
    }

    private boolean isValidRegisterRequest(RegisterRequest registerRequest) {
        return registerRequest.getUsername() != null &&
                registerRequest.getPassword() != null &&
                registerRequest.getEmail() != null &&
                !registerRequest.getUsername().isEmpty() &&
                !registerRequest.getPassword().isEmpty() &&
                !registerRequest.getEmail().isEmpty();
    }

    private void publishUserLoginEvent(String username) {
        try {
            UserLoginEvent event = new UserLoginEvent(username, System.currentTimeMillis());
            rabbitTemplate.convertAndSend(
                    RabbitConfig.USER_EXCHANGE,
                    RabbitConfig.USER_LOGIN_ROUTING_KEY,
                    event);
            logger.info("Published user login event for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to publish user login event", e);
        }
    }

    private void publishUserRegistrationEvent(RegisterRequest registerRequest) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.USER_EXCHANGE,
                    RabbitConfig.USER_REGISTER_ROUTING_KEY,
                    registerRequest);
            logger.info("Published user registration event for user: {}", registerRequest.getUsername());
        } catch (Exception e) {
            logger.error("Failed to publish user registration event", e);
            throw new RuntimeException("Failed to process registration request");
        }
    }
}