package com.example.userservice.messaging;

import com.example.userservice.dto.AuthenticationRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationListener.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${rabbitmq.queues.user-authenticate}")
    public void handleAuthenticationRequest(@Payload AuthenticationRequest request,
            @Header("correlationId") String correlationId,
            @Header("replyTo") String replyTo) {
        logger.info("Received authentication request for: {} with correlationId: {}",
                request.getEmail(), correlationId);

        try {
            // Authenticate the user
            UserDto userDto = userService.authenticateUserByEmail(request.getEmail(), request.getPassword());

            // Send back the response
            rabbitTemplate.convertAndSend("", replyTo, userDto, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });

            logger.info("Sent authentication response for user: {} with correlationId: {}",
                    request.getEmail(), correlationId);
        } catch (Exception e) {
            logger.error("Error processing authentication request: {}", e.getMessage());
            // Send an error response
            rabbitTemplate.convertAndSend("", replyTo, null, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }
}