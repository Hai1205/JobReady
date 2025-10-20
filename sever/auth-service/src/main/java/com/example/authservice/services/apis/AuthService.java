package com.example.authservice.services.apis;

import com.example.authservice.configs.MailConfig;
import com.example.authservice.dtos.*;
import com.example.authservice.dtos.requests.*;
import com.example.authservice.dtos.responses.*;
import com.example.authservice.exceptions.OurException;
import com.example.authservice.services.OtpService;
import com.example.authservice.services.producers.UserProducer;
import com.example.authservice.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private JwtUtil jwtUtil;
    private MailConfig mailConfig;
    private UserProducer userProducer;
    private OtpService otpService;
    private final ObjectMapper objectMapper;

    public AuthService(JwtUtil jwtUtil, MailConfig mailConfig, UserProducer userProducer, OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.mailConfig = mailConfig;
        this.userProducer = userProducer;
        this.otpService = otpService;
        this.objectMapper = new ObjectMapper();
    }

    public Response login(String dataJson, HttpServletResponse httpServletResponse) {
        Response response = new Response();

        try {
            LoginRequest request = objectMapper.readValue(dataJson, LoginRequest.class);
            String email = request.getEmail();
            String password = request.getPassword();

            UserDto userDto = userProducer.authenticateUser(email, password);

            if (userDto == null) {
                throw new OurException("Invalid credentials", 404);
            }

            boolean isPending = userDto.getStatus().equals("pending");
            if (isPending) {
                throw new OurException("Account not verified. Please verify your account before logging in.", 403);
            }

            boolean isBanned = userDto.getStatus().equals("banned");
            if (isBanned) {
                throw new OurException("Account is banned. Please contact support.", 403);
            }

            String userId = userDto.getId().toString();
            String username = userDto.getUsername();
            String role = userDto.getRole();

            String accessToken = jwtUtil.generateAccessToken(userId, email, role, username);
            String refreshToken = jwtUtil.generateRefreshToken(userId, email, username);

            Cookie accessTokenCookie = handleCreateHttpOnlyCookie("access_token", accessToken, 5 * 60 * 60); // 5 hours
            Cookie refreshTokenCookie = handleCreateHttpOnlyCookie("refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7
                                                                                                                     // days
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", accessToken);
            httpServletResponse.setHeader("X-Refresh-Token", refreshToken);

            response.setMessage("Login successful");
            response.setUser(userDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    private Cookie handleCreateHttpOnlyCookie(String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // Set to false to allow JavaScript access if needed
        cookie.setSecure(false); // Set to false for localhost development (change to true in production with
                                 // HTTPS)
        cookie.setPath("/"); // Set the path to root to make it accessible across the domain
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setAttribute("SameSite", "Lax"); // Add SameSite attribute for better compatibility
        return cookie;
    }

    public Response validateToken(String token, String username) {
        Response response = new Response();

        try {
            boolean isValid = jwtUtil.validateToken(token, username);

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("valid", isValid);

            response.setMessage("Token validation successful");
            response.setAdditionalData(additionalData);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response register(String dataJson) {
        Response response = new Response();

        try {
            RegisterRequest request = objectMapper.readValue(dataJson, RegisterRequest.class);
            String username = request.getUsername();
            String email = request.getEmail();
            String password = request.getPassword();
            String fullname = request.getFullname();

            UserDto userDto = userProducer.createUser(username, email, password, fullname);

            response.setMessage("Registration successful");
            response.setUser(userDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response verifyOTP(String email, String dataJson) {
        Response response = new Response();

        try {
            VerifyOtpRequest request = objectMapper.readValue(dataJson, VerifyOtpRequest.class);
            String otp = request.getOtp();

            boolean isValid = otpService.validateOtp(email, otp);

            if (!isValid) {
                throw new OurException("Invalid OTP.");
            }

            if (request.getIsActivation()) {
                userProducer.activateUser(email);
            }

            response.setMessage("Otp verified successfully!");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response sendOTP(String email) {
        Response response = new Response();

        try {
            String otp = otpService.generateOtp(email);

            UserDto userDto = userProducer.findUserByEmail(email);
            if (userDto == null) {
                throw new OurException("User not found.", 404);
            }

            String subject = "Account Activation";
            String templateName = "mail_active_account";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientEmail", email);
            variables.put("recipientName", email);
            variables.put("senderName", "JobReady");
            variables.put("otp", otp);
            mailConfig.sendMail(email, subject, templateName, variables);

            response.setMessage("OTP is sent!");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response changePassword(String email, String dataJson) {
        Response response = new Response();

        try {
            ChangePasswordRequest request = objectMapper.readValue(dataJson, ChangePasswordRequest.class);
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            userProducer.changePasswordUser(email, currentPassword, newPassword);

            response.setMessage("Password changed successfully!");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
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
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response forgotPassword(String email, String dataJson) {
        Response response = new Response();

        try {
            ChangePasswordRequest request = objectMapper.readValue(dataJson, ChangePasswordRequest.class);
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                throw new OurException("Password does not match.");
            }

            userProducer.forgotPasswordUser(email, newPassword);

            response.setMessage("Password updated successfully!");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
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

            Cookie accessTokenCookie = handleCreateHttpOnlyCookie("access_token", newAccessToken, 15 * 60); // 15
                                                                                                            // minutes
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", newAccessToken);

            response.setMessage("Token refreshed successfully!");
            response.setUser(userDto);

            logger.info("Token refreshed successfully for user: {}", userDto.getUsername());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response logout(HttpServletResponse httpServletResponse) {
        Response response = new Response();

        try {
            Cookie accessTokenCookie = handleCreateHttpOnlyCookie("access_token", "", 15 * 60);
            Cookie refreshTokenCookie = handleCreateHttpOnlyCookie("refresh_token", "", 7 * 24 * 60 * 60);
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", "");
            httpServletResponse.setHeader("X-Refresh-Token", "");

            response.setMessage("Logged out successfully");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }
}