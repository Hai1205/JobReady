package com.example.authservice.service;

import com.example.authservice.config.MailConfig;
import com.example.authservice.config.RabbitConfig;
import com.example.authservice.dto.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.dto.responses.*;
import com.example.authservice.event.UserLoginEvent;
import com.example.authservice.exception.OurException;
import com.example.authservice.util.JwtUtil;
import com.example.authservice.util.Utils;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MailConfig mailConfig;

    // TODO: Implement Redis integration for OTP storage

    public Response login(String email, String password) {
        Response response = new Response();

        try {
            // Validate input first
            if (!isValidInput(email, password)) {
                throw new RuntimeException("Invalid credentials format");
            }

            // Check user credentials with user-service using RabbitMQ
            UserDto userDto = validateUserCredentials(email, password);

            if (userDto != null) {
                // Check user status
                String userStatus = userDto.getStatus();
                if ("PENDING".equals(userStatus)) {
                    throw new RuntimeException("Account not verified. Please verify your account before logging in.");
                }

                // Get user ID for token
                String userId = userDto.getId().toString();
                String username = userDto.getUsername();
                String role = userDto.getRole();

                String token = jwtUtil.generateToken(userId, username, role);

                // Publish login event to RabbitMQ
                publishUserLoginEvent(username);

                Data data = new Data();
                data.setToken(token);
                data.setUser(userDto);

                response.setStatusCode(200);
                response.setMessage("Login successful");
                response.setData(data);
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } catch (RuntimeException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    private UserDto validateUserCredentials(String email, String password) {
        try {
            // Create a unique correlation ID for this request
            String correlationId = java.util.UUID.randomUUID().toString();

            // Create the authentication request
            AuthenticationRequest request = new AuthenticationRequest(email, password);
            request.setCorrelationId(correlationId);

            // Create a BlockingQueue to receive the response
            java.util.concurrent.BlockingQueue<UserDto> responseQueue = new java.util.concurrent.ArrayBlockingQueue<>(
                    1);

            // Setup a reply consumer
            String replyQueueName = RabbitConfig.USER_AUTHENTICATE_REPLY_QUEUE;

            // Create a correlation ID-specific consumer
            org.springframework.amqp.core.MessageListener messageListener = message -> {
                String receivedCorrelationId = message.getMessageProperties().getCorrelationId();
                if (correlationId.equals(receivedCorrelationId)) {
                    try {
                        UserDto userDto = (UserDto) rabbitTemplate.getMessageConverter().fromMessage(message);
                        responseQueue.offer(userDto);
                    } catch (Exception e) {
                        logger.error("Error processing authentication reply", e);
                    }
                }
            };

            // Create a container for listening to responses
            org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer container = new org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer(
                    rabbitTemplate.getConnectionFactory());
            container.setQueueNames(replyQueueName);
            container.setMessageListener(messageListener);
            container.start();

            try {
                // Send the authentication request with the correlation ID
                rabbitTemplate.convertAndSend(
                        RabbitConfig.USER_EXCHANGE,
                        RabbitConfig.USER_AUTHENTICATE_ROUTING_KEY,
                        request,
                        message -> {
                            message.getMessageProperties().setCorrelationId(correlationId);
                            message.getMessageProperties().setReplyTo(replyQueueName);
                            return message;
                        });

                logger.info("Sent authentication request for user: {} with correlationId: {}", email, correlationId);

                // Wait for the response with a timeout
                UserDto userDto = responseQueue.poll(10, java.util.concurrent.TimeUnit.SECONDS);
                if (userDto != null) {
                    return userDto;
                } else {
                    throw new RuntimeException("Authentication timeout");
                }
            } finally {
                // Always clean up the container
                container.stop();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Authentication process was interrupted", e);
            throw new RuntimeException("Authentication process was interrupted");
        } catch (Exception e) {
            logger.error("Error validating user credentials for: {}", email, e);
            throw new RuntimeException("Authentication service error: " + e.getMessage());
        }
    }

    private boolean isValidInput(String email, String password) {
        return email != null && password != null && !email.isEmpty() && !password.isEmpty();
    }

    public Response validateToken(String token, String username) {
        Response response = new Response();

        try {
            boolean isValid = jwtUtil.validateToken(token, username);

            Data data = new Data();
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("valid", isValid);
            data.setAdditionalData(additionalData);

            response.setStatusCode(200);
            response.setMessage("Token validation successful");
            response.setData(data);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response register(String email, String password) {
        Response response = new Response();

        try {
            // Validate registration request
            if (!isValidInput(email, password)) {
                throw new RuntimeException("Invalid credentials format");
            }

            // Publish registration event to RabbitMQ for user-service to create user
            publishUserRegistrationEvent(email, password);

            response.setStatusCode(200);
            response.setMessage("User registration request sent successfully");
        } catch (RuntimeException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response verifyOTP(String email, String otp) {
        Response response = new Response();

        try {
            // TODO: Implement OTP verification with RabbitMQ
            // For now, we'll just assume OTP verification is successful
            response.setStatusCode(200);
            response.setMessage("Your account is activated!");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response sendOTP(String email) {
        Response response = new Response();

        try {
            // Sử dụng Util để tạo OTP
            String otp = Utils.generateOTP(6);

            String subject = "Account Activation";
            String templateName = "mail_active_account";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientEmail", email);
            variables.put("recipientName", email);
            variables.put("senderName", "JobReady");
            variables.put("otp", otp);

            try {
                mailConfig.sendMail(email, subject, templateName, variables);
                response.setStatusCode(200);
                response.setMessage("OTP is sent!");
            } catch (MessagingException mailException) {
                logger.error("Error sending mail: {}", mailException.getMessage(), mailException);
                response.setStatusCode(500);
                response.setMessage("Failed to send OTP email: " + mailException.getMessage());
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response changePassword(String userId, String currentPassword, String newPassword, String rePassword) {
        Response response = new Response();

        try {
            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            // TODO: Implement change password with RabbitMQ
            // For now, we'll just assume the password change is successful
            response.setStatusCode(200);
            response.setMessage("Password changed successfully!");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response resetPassword(String userId) {
        Response response = new Response();

        try {
            // TODO: Implement reset password with RabbitMQ

            // For now, we'll just assume the password reset is successful
            response.setStatusCode(200);
            response.setMessage("Password reset email sent successfully!");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response forgotPassword(String email, String newPassword, String rePassword) {
        Response response = new Response();

        try {
            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            // TODO: Implement forgot password with RabbitMQ

            // For now, we'll just assume the password update is successful
            response.setStatusCode(200);
            response.setMessage("Password updated successfully!");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
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

    private void publishUserRegistrationEvent(String email, String password) {
        try {
            Map<String, String> registerRequest = new HashMap<>();
            registerRequest.put("email", email);
            registerRequest.put("password", password);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.USER_EXCHANGE,
                    RabbitConfig.USER_REGISTER_ROUTING_KEY,
                    registerRequest);
            logger.info("Published user registration event for user: {}", email);
        } catch (Exception e) {
            logger.error("Failed to publish user registration event", e);
            throw new RuntimeException("Failed to process registration request");
        }
    }
}