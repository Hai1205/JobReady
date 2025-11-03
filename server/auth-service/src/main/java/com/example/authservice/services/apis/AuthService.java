package com.example.authservice.services.apis;

import com.example.authservice.configs.MailConfig;
import com.example.authservice.dtos.*;
import com.example.authservice.dtos.requests.*;
import com.example.authservice.dtos.responses.*;
import com.example.authservice.exceptions.OurException;
import com.example.authservice.services.OtpService;
import com.example.authservice.services.producers.AuthProducer;
import com.example.authservice.utils.JwtUtil;
import com.example.rabbitcommon.dtos.RPCResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private AuthProducer authProducer;
    private OtpService otpService;
    private final ObjectMapper objectMapper;

    public AuthService(JwtUtil jwtUtil, MailConfig mailConfig, AuthProducer authProducer, OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.mailConfig = mailConfig;
        this.authProducer = authProducer;
        this.otpService = otpService;
        this.objectMapper = new ObjectMapper();
    }

    public Response login(String dataJson, HttpServletResponse httpServletResponse) {
        logger.info("Login attempt");
        Response response = new Response();
        LoginRequest request = null;

        try {
            request = objectMapper.readValue(dataJson, LoginRequest.class);
            String identifier = request.getIdentifier();
            String password = request.getPassword();

            RPCResponse<UserDto> rpcResponse = authProducer.authenticateUser(identifier, password);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("Login failed: Invalid credentials for username or email: {}", identifier);
                throw new OurException("Invalid credentials", 404);
            }

            boolean isPending = user.getStatus().equals("pending");
            if (isPending) {
                logger.warn("Login blocked: Account not verified for username or email: {}", identifier);
                throw new OurException("Account not verified. Please verify your account before logging in.", 403);
            }

            boolean isBanned = user.getStatus().equals("banned");
            if (isBanned) {
                logger.warn("Login blocked: Account banned for username or email: {}", identifier);
                throw new OurException("Account is banned. Please contact support.", 405);
            }

            String userId = user.getId().toString();
            String email = user.getEmail();
            String username = user.getUsername();
            String role = user.getRole();

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
            response.setUser(user);
            logger.info("Login successful for user: {} (ID: {})", email, userId);
            return response;
        } catch (OurException e) {
            logger.error("Login failed with OurException for identifier {}: {}",
                    request != null ? request.getIdentifier() : "unknown", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Login failed with unexpected error for identifier {}",
                    request != null ? request.getIdentifier() : "unknown", e);
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
        logger.debug("Token validation attempt for username: {}", username);
        Response response = new Response();

        try {
            boolean isValid = jwtUtil.validateToken(token, username);

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("valid", isValid);

            response.setMessage("Token validation successful");
            response.setAdditionalData(additionalData);

            if (isValid) {
                logger.debug("Token validation successful for username: {}", username);
            } else {
                logger.warn("Token validation failed for username: {}", username);
            }

            return response;
        } catch (OurException e) {
            logger.error("Token validation failed with OurException for username {}: {}", username, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Token validation failed with unexpected error for username {}", username, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response register(String dataJson) {
        logger.info("Registration attempt");
        Response response = new Response();

        try {
            RegisterRequest request = objectMapper.readValue(dataJson, RegisterRequest.class);
            String username = request.getUsername();
            String email = request.getEmail();
            String password = request.getPassword();
            String fullname = request.getFullname();

            RPCResponse<UserDto> rpcResponse = authProducer.createUser(username, email, password, fullname);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            response.setMessage("Registration successful");
            response.setUser(user);
            logger.info("Registration successful for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Registration failed with OurException for email");
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Registration failed with unexpected error for email");
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response verifyOTP(String identifier, String dataJson) {
        logger.info("OTP verification attempt for identifier: {}", identifier);
        Response response = new Response();

        try {
            RPCResponse<UserDto> rpcResponse = authProducer.findUserByIdentifier(identifier);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("OTP verification failed: User not found for identifier: {}", identifier);
                throw new OurException("User not found.", 404);
            }

            String email = user.getEmail();

            VerifyOtpRequest request = objectMapper.readValue(dataJson, VerifyOtpRequest.class);
            String otp = request.getOtp();

            boolean isValid = otpService.validateOtp(email, otp);

            if (!isValid) {
                logger.warn("OTP verification failed: Invalid OTP for email: {}", email);
                throw new OurException("Invalid OTP.");
            }

            if (request.getIsActivation()) {
                authProducer.activateUser(email);
                logger.info("User account activated for email: {}", email);
            }

            response.setMessage("Otp verified successfully!");
            logger.info("OTP verification successful for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("OTP verification failed with OurException for identifier {}: {}", identifier, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("OTP verification failed with unexpected error for identifier {}", identifier, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response sendOTP(String identifier) {
        logger.info("Sending OTP to identifier: {}", identifier);
        Response response = new Response();

        try {
            RPCResponse<UserDto> rpcResponse = authProducer.findUserByIdentifier(identifier);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("OTP send failed: User not found for identifier: {}", identifier);
                throw new OurException("User not found.", 404);
            }

            String email = user.getEmail();

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
            logger.info("OTP sent successfully to email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("OTP send failed with OurException for identifier {}: {}", identifier, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("OTP send failed with unexpected error for identifier {}", identifier, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response changePassword(String identifier, String dataJson) {
        logger.info("Password change attempt for identifier: {}", identifier);
        Response response = new Response();

        try {
            RPCResponse<UserDto> rpcResponse = authProducer.findUserByIdentifier(identifier);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("OTP send failed: User not found for identifier: {}", identifier);
                throw new OurException("User not found.", 404);
            }

            ChangePasswordRequest request = objectMapper.readValue(dataJson, ChangePasswordRequest.class);
            String email = user.getEmail();
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                logger.warn("Password change failed: Password mismatch for email: {}", email);
                throw new OurException("Password does not match.");
            }

            authProducer.changePasswordUser(email, currentPassword, newPassword);

            response.setMessage("Password changed successfully!");
            logger.info("Password changed successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Password change failed with OurException for identifier {}: {}", identifier, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Password change failed with unexpected error for identifier {}", identifier, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response resetPassword(String email) {
        logger.info("Password reset attempt for email: {}", email);
        Response response = new Response();

        try {
            RPCResponse<String> rpcResponse = authProducer.resetPasswordUser(email);
            String newPassword = (String) rpcResponse.getData();

            String subject = "Reset Password";
            String templateName = "mail_reset_password";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientEmail", email);
            variables.put("recipientName", email);
            variables.put("senderName", "JobReady");
            variables.put("password", newPassword);

            mailConfig.sendMail(email, subject, templateName, variables);

            response.setMessage("Password reset email sent successfully!");
            logger.info("Password reset email sent successfully to: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Password reset failed with OurException for email {}: {}", email, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Password reset failed with unexpected error for email {}", email, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response forgotPassword(String identifier, String dataJson) {
        logger.info("Forgot password attempt for identifier: {}", identifier);
        Response response = new Response();

        try {
            RPCResponse<UserDto> rpcResponse = authProducer.findUserByIdentifier(identifier);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("OTP send failed: User not found for identifier: {}", identifier);
                throw new OurException("User not found.", 404);
            }

            ChangePasswordRequest request = objectMapper.readValue(dataJson, ChangePasswordRequest.class);
            String email = user.getEmail();
            String newPassword = request.getNewPassword();
            String rePassword = request.getRePassword();

            if (!newPassword.equals(rePassword)) {
                logger.warn("Forgot password failed: Password mismatch for email: {}", email);
                throw new OurException("Password does not match.");
            }

            authProducer.forgotPasswordUser(email, newPassword);

            response.setMessage("Password updated successfully!");
            logger.info("Password updated successfully via forgot password for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Forgot password failed with OurException for identifier {}: {}", identifier, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Forgot password failed with unexpected error for identifier {}", identifier, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response refreshToken(
            RefreshTokenRequest request,
            String authHeader,
            HttpServletRequest httpRequest,
            HttpServletResponse httpServletResponse) {

        logger.info("Token refresh attempt");
        Response response = new Response();

        try {
            String refreshToken = null;
            // Thử lấy refresh token từ request body
            if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isEmpty()) {
                refreshToken = request.getRefreshToken();
                logger.debug("Refresh token obtained from request body");
            }
            // Thử lấy từ Authorization header
            else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
                logger.debug("Refresh token obtained from Authorization header");
            }
            // Thử lấy từ cookie
            else if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("refresh_token".equals(cookie.getName()) && cookie.getValue() != null
                            && !cookie.getValue().isEmpty()) {
                        refreshToken = cookie.getValue();
                        logger.debug("Refresh token obtained from cookie");
                        break;
                    }
                }
            }

            if (refreshToken == null || refreshToken.isEmpty()) {
                logger.warn("Token refresh failed: No refresh token provided");
                throw new OurException("Refresh token is required", 400);
            }

            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                logger.warn("Token refresh failed: Invalid or expired refresh token");
                throw new OurException("Invalid or expired refresh token", 401);
            }

            String email = jwtUtil.extractEmail(refreshToken);
            String userId = jwtUtil.extractUserId(refreshToken);

            RPCResponse<UserDto> rpcResponse = authProducer.findUserByEmail(email);
            UserDto user = objectMapper.convertValue(rpcResponse.getData(), UserDto.class);

            if (user == null) {
                logger.warn("Token refresh failed: User not found for email: {}", email);
                throw new OurException("User not found", 404);
            }

            String newAccessToken = jwtUtil.generateAccessToken(userId, email, user.getRole(),
                    user.getUsername());

            Cookie accessTokenCookie = handleCreateHttpOnlyCookie("access_token", newAccessToken, 15 * 60); // 15
                                                                                                            // minutes
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", newAccessToken);

            response.setMessage("Token refreshed successfully!");
            response.setUser(user);

            logger.info("Token refreshed successfully for user: {}", user.getUsername());
            return response;
        } catch (OurException e) {
            logger.error("Token refresh failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Token refresh failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response logout(HttpServletResponse httpServletResponse) {
        logger.info("User logout attempt");
        Response response = new Response();

        try {
            Cookie accessTokenCookie = handleCreateHttpOnlyCookie("access_token", "", 15 * 60);
            Cookie refreshTokenCookie = handleCreateHttpOnlyCookie("refresh_token", "", 7 * 24 * 60 * 60);
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);
            httpServletResponse.setHeader("X-Access-Token", "");
            httpServletResponse.setHeader("X-Refresh-Token", "");

            response.setMessage("Logged out successfully");
            logger.info("User logged out successfully");
            return response;
        } catch (OurException e) {
            logger.error("Logout failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Logout failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }
}