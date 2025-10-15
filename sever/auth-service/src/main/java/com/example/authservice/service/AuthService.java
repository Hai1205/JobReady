package com.example.authservice.service;

import com.example.authservice.config.MailConfig;
import com.example.authservice.dto.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.dto.responses.*;
import com.example.authservice.exception.OurException;
import com.example.authservice.service.producers.UserProducer;
import com.example.authservice.util.JwtUtil;

import org.slf4j.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private JwtUtil jwtUtil;
    private MailConfig mailConfig;
    private UserProducer userProducer;
    private OtpService otpService;

    public AuthService(JwtUtil jwtUtil, MailConfig mailConfig, UserProducer userProducer, OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.mailConfig = mailConfig;
        this.userProducer = userProducer;
        this.otpService = otpService;
    }

    public Response login(LoginRequest request) {
        Response response = new Response();

        try {
            UserDto userDto = userProducer.authenticateUser(request.getEmail(), request.getPassword());

            if (userDto == null) {
                throw new OurException("Invalid credentials", 404);
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

    public Response register(RegisterRequest request) {
        Response response = new Response();

        try {
            UserDto userDto = userProducer.createUser(request.getEmail(), request.getPassword(),
                    request.getFullname());

            ResponseData data = new ResponseData();
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("Registration successful");
            response.setData(data);
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
            boolean isValid = otpService.validateOtp(email, otp);

            if (!isValid) {
                throw new OurException("Invalid OTP.");
            }

            response.setStatusCode(200);
            response.setMessage("Your account is activated!");
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

    public Response sendOTP(String email) {
        Response response = new Response();

        try {
            String otp = otpService.generateOtp(email);

            String subject = "Account Activation";
            String templateName = "mail_active_account";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientEmail", email);
            variables.put("recipientName", email);
            variables.put("senderName", "JobReady");
            variables.put("otp", otp);

            mailConfig.sendMail(email, subject, templateName, variables);
            response.setStatusCode(200);
            response.setMessage("OTP is sent!");
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

    public Response changePassword(String email, ChangePasswordRequest request) {
        Response response = new Response();

        try {
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            userProducer.changePasswordUser(email, currentPassword, newPassword);

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

    public Response resetPassword(String email) {
        Response response = new Response();

        try {
            String password = userProducer.resetPasswordUser(email);

            String subject = "Reset Password";
            String templateName = "mail_reset_password";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientEmail", email);
            variables.put("recipientName", email);
            variables.put("senderName", "JobReady");
            variables.put("password", password);

            mailConfig.sendMail(email, subject, templateName, variables);

            response.setStatusCode(200);
            response.setMessage("Password reset email sent successfully!");
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

    public Response forgotPassword(String email, ChangePasswordRequest request) {
        Response response = new Response();

        try {
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            userProducer.forgotPasswordUser(email, newPassword);

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
    
    public Response refreshToken() {
        Response response = new Response();

        try {

            response.setStatusCode(200);
            response.setMessage("Refresh token successfully!");
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