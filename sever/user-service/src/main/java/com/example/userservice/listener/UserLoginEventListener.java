package com.example.userservice.listener;

import com.example.userservice.event.UserLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserLoginEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginEventListener.class);

    @RabbitListener(queues = "user.login.queue")
    public void handleUserLoginEvent(UserLoginEvent event) {
        logger.info("Received user login event: {}", event);
        logger.info("User {} logged in at timestamp: {}", event.getUsername(), event.getTimestamp());
        
        // Additional business logic can be added here
        // For example: update last login time, send welcome email, etc.
    }
}