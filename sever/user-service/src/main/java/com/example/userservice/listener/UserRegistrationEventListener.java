package com.example.userservice.listener;

import com.example.userservice.config.RabbitConfig;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationEventListener.class);

    @Autowired
    private UserService userService;

    @RabbitListener(queues = RabbitConfig.USER_REGISTER_QUEUE)
    public void handleUserRegistration(RegisterRequest registerRequest) {
        try {
            logger.info("Received user registration request for user: {}", registerRequest.getUsername());

            // Create new user DTO from registration request
            UserDto userDto = new UserDto(
                    null, // ID will be generated
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName());
            // Set password separately since constructor doesn't include it
            userDto.setPassword(registerRequest.getPassword()); // In production, this should be hashed

            // Save user through UserService
            UserDto savedUser = userService.createUser(userDto);

            logger.info("Successfully created user with ID: {} for username: {}",
                    savedUser.getId(), savedUser.getUsername());

        } catch (Exception e) {
            logger.error("Failed to create user for registration request: {}",
                    registerRequest.getUsername(), e);
            // In production, you might want to send this to a dead letter queue
            // or implement retry logic
        }
    }
}