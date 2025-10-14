package com.example.userservice.messaging;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.requests.AuthenticationRequest;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationListener.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${rabbitmq.queues.user-authenticate}")
    public void handleAuthenticationRequest(@Payload AuthenticationRequest request, Message amqpMessage) {
        String correlationId = amqpMessage.getMessageProperties().getCorrelationId();
        String replyTo = amqpMessage.getMessageProperties().getReplyTo();

        logger.info("Received authentication request for: {} with correlationId: {}",
                request.getEmail(), correlationId);

        try {
            // Authenticate the user
            UserDto userDto = userService.authenticateUserByEmail(request.getEmail(), request.getPassword());

            // Check if authentication was successful
            if (userDto != null) {
                // Send back the successful response
                rabbitTemplate.convertAndSend("", replyTo, userDto, message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    return message;
                });

                logger.info("Sent authentication response for user: {} with correlationId: {}",
                        request.getEmail(), correlationId);
            } else {
                // Handle null response from service (authentication failed)
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication failed: Invalid credentials");

                rabbitTemplate.convertAndSend("", replyTo, errorResponse, message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    return message;
                });

                logger.warn("Authentication failed for user: {} with correlationId: {}",
                        request.getEmail(), correlationId);
            }
        } catch (Exception e) {
            logger.error("Error processing authentication request: {}", e.getMessage());
            // Create an error response object instead of sending null
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication failed: " + e.getMessage());

            // Send the error response
            rabbitTemplate.convertAndSend("", replyTo, errorResponse, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }
}