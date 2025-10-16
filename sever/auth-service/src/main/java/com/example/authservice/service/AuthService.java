package com.example.authservice.service;

import com.example.authservice.config.MailConfig;
import com.example.authservice.dto.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.dto.responses.*;
import com.example.authservice.exception.OurException;
import com.example.authservice.service.producers.UserProducer;
import com.example.authservice.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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

    public Response login(LoginRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();

        try {
            UserDto userDto = userProducer.authenticateUser(request.getEmail(), request.getPassword());

            if (userDto == null) {
                throw new OurException("Invalid credentials", 404);
            }

            boolean isPending = userDto.getStatus().equals("PENDING");
            if (isPending) {
                throw new OurException("Account not verified. Please verify your account before logging in.", 403);
            }

            boolean isBanned = userDto.getStatus().equals("BANNED");
            if (isBanned) {
                throw new OurException("Account is banned. Please contact support.", 403);
            }

            String userId = userDto.getId().toString();
            String username = userDto.getUsername();
            String email = userDto.getEmail();
            String role = userDto.getRole();

            String accessToken = jwtUtil.generateAccessToken(userId, email, role, username);
            String refreshToken = jwtUtil.generateRefreshToken(userId, email, username);

            Cookie accessTokenCookie = createHttpOnlyCookie("ACCESS_TOKEN", accessToken, 15 * 60); // 15 minutes
            Cookie refreshTokenCookie = createHttpOnlyCookie("REFRESH_TOKEN", refreshToken, 7 * 24 * 60 * 60); // 7 days
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", accessToken);
            httpServletResponse.setHeader("X-Refresh-Token", refreshToken);

            ResponseData data = new ResponseData();
            data.setUser(userDto);

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

    private Cookie createHttpOnlyCookie(String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Ensure the cookie is only sent over HTTPS
        cookie.setPath("/"); // Set the path to root to make it accessible across the domain
        cookie.setMaxAge(maxAgeInSeconds);
        return cookie;
    }

    public Response validateToken(String token, String username) {
        Response response = new Response();

        try {
            boolean isValid = jwtUtil.validateToken(token, username);

            ResponseData data = new ResponseData();
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("valid", isValid);
            data.setAdditionalData(additionalData);

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

    public Response refreshToken(
            RefreshTokenRequest request,
            String authHeader,
            HttpServletResponse httpServletResponse) {

        Response response = new Response();

        try {
            String refreshToken = null;
            if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isEmpty()) {
                refreshToken = request.getRefreshToken();
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
            }

            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new OurException("Refresh token is required", 400);
            }

            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                throw new OurException("Invalid or expired refresh token", 401);
            }

            String email = jwtUtil.extractEmail(refreshToken);
            String userId = jwtUtil.extractUserId(refreshToken);
            UserDto userDto = userProducer.findUserByEmail(email);

            if (userDto == null) {
                throw new OurException("User not found", 404);
            }

            String newAccessToken = jwtUtil.generateAccessToken(userId, email, userDto.getRole(),
                    userDto.getUsername());

            Cookie accessTokenCookie = createHttpOnlyCookie("ACCESS_TOKEN", newAccessToken, 15 * 60); // 15 minutes
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", newAccessToken);

            ResponseData data = new ResponseData();
            data.setUser(userDto);

            response.setMessage("Token refreshed successfully!");
            response.setData(data);

            logger.info("Token refreshed successfully for user: {}", userDto.getUsername());
            return response;
        } catch (OurException e) {
            logger.error("Refresh token error: {}", e.getMessage());
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh", e);
            response.setStatusCode(500);
            response.setMessage("Internal server error: " + e.getMessage());
            return response;
        }
    }

    public Response logout(HttpServletResponse httpServletResponse) {
        Response response = new Response();

        try {
            Cookie accessTokenCookie = createHttpOnlyCookie("ACCESS_TOKEN", "", 15 * 60);
            Cookie refreshTokenCookie = createHttpOnlyCookie("REFRESH_TOKEN", "", 7 * 24 * 60 * 60);
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", "");
            httpServletResponse.setHeader("X-Refresh-Token", "");

            response.setMessage("Logged out successfully");
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
}