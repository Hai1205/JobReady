package com.example.authservice.service;

import com.example.authservice.config.MailConfig;
import com.example.authservice.dto.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.dto.responses.*;
import com.example.authservice.exception.OurException;
import com.example.authservice.util.JwtUtil;
import com.example.authservice.util.Utils;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private MailConfig mailConfig;

    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try {
            // Validate input first
            if (!isValidInput(loginRequest.getEmail(), loginRequest.getPassword())) {
                throw new RuntimeException("Invalid credentials format");
            }

            UserDto userDto = new UserDto(); // Giả lập việc lấy userDto từ user-service

            if (userDto == null) {
                throw new OurException("User not found", 404);
            }

            // Check user status
            String userStatus = userDto.getStatus();
            if ("PENDING".equals(userStatus)) {
                throw new OurException("Account not verified. Please verify your account before logging in.");
            }

            // Get user ID for token
            String userId = userDto.getId().toString();
            String username = userDto.getUsername();
            String role = userDto.getRole();

            String token = jwtUtil.generateToken(userId, username, role);

            ResponseData data = new ResponseData();
            data.setToken(token);
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("Login successful");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    private boolean isValidInput(String email, String password) {
        return email != null && password != null && !email.isEmpty() && !password.isEmpty();
    }

    public Response validateToken(String token, String username) {
        Response response = new Response();

        try {
            boolean isValid = jwtUtil.validateToken(token, username);

            ResponseData data = new ResponseData();
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
}