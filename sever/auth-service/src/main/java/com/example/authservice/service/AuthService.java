package com.example.authservice.service;

import com.example.authservice.config.RabbitConfig;
import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.event.UserLoginEvent;
import com.example.authservice.util.JwtUtil;
import com.example.dto.Data;
import com.example.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Response authenticate(AuthRequest authRequest) {
        Response response = new Response();

        try {
            // Validate input first
            if (!isValidInput(authRequest)) {
                throw new RuntimeException("Invalid credentials format");
            }

            // Check user credentials with user-service
            Map<String, Object> userResponse = validateUserCredentials(authRequest.getUsername(),
                    authRequest.getPassword());

            if (userResponse != null) {
                // Check user status
                String userStatus = (String) userResponse.get("status");
                if ("PENDING".equals(userStatus)) {
                    throw new RuntimeException("Account not verified. Please verify your account before logging in.");
                }

                // Get user ID for token
                String userId = userResponse.get("id").toString();

                String token = jwtUtil.generateToken(authRequest.getUsername(), userId);

                // Publish login event to RabbitMQ
                publishUserLoginEvent(authRequest.getUsername());

                Data data = new Data();
                data.setToken(token);
                data.setUser(userResponse);
                data.setRole((String) userResponse.get("role"));
                data.setStatus((String) userResponse.get("status"));

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

    @Value("${USER_SERVICE_BASE_URL}")
    private String userServiceBaseUrl;

    private Map<String, Object> validateUserCredentials(String username, String password) {
        try {
            // Call user-service to validate credentials
            String url = userServiceBaseUrl + "/users/authenticate";

            Map<String, String> request = new HashMap<>();
            request.put("username", username);
            request.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map<String, Object>> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("Invalid credentials for user: {}", username);
            return null;
        } catch (Exception e) {
            logger.error("Error validating user credentials for: {}", username, e);
            throw new RuntimeException("Authentication service error");
        }

        return null;
    }

    private boolean isValidInput(AuthRequest authRequest) {
        return authRequest != null &&
                authRequest.getUsername() != null &&
                authRequest.getPassword() != null &&
                !authRequest.getUsername().isEmpty() &&
                !authRequest.getPassword().isEmpty();
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

    public Response registerUser(RegisterRequest registerRequest) {
        Response response = new Response();

        try {
            // Validate registration request
            if (!isValidRegisterRequest(registerRequest)) {
                throw new RuntimeException("Invalid registration data");
            }

            // Publish registration event to RabbitMQ for user-service to create user
            publishUserRegistrationEvent(registerRequest);

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