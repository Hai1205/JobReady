package com.example.authservice.services;

import com.example.authservice.dtos.UserDto;
import com.example.authservice.dtos.requests.*;
import com.example.authservice.dtos.responses.*;
import com.example.authservice.exceptions.OurException;
import com.example.authservice.services.apis.AuthApi;
import com.example.authservice.services.feigns.UserFeignClient;
import com.example.authservice.services.rabbitmqs.producers.AuthProducer;
import com.example.authservice.services.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtUtil;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private AuthProducer authProducer;

    @Mock
    private OtpService otpService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthApi authService;

    private ObjectMapper objectMapper;
    private UserDto mockUser;
    private Response mockUserResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Mock user data
        mockUser = new UserDto();
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        UUID id = UUID.fromString(idStr);
        mockUser.setId(id);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setRole("user");
        mockUser.setStatus("active");

        // Mock response containing user
        mockUserResponse = new Response();
        mockUserResponse.setUser(mockUser);
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        AuthenticateUserRequest authRequest = new AuthenticateUserRequest("testuser", "password123");
        when(userFeignClient.authenticateUser("testuser", "password123")).thenReturn(mockUserResponse);
        when(jwtUtil.generateAccessToken(anyString(), anyString(), anyString(), anyString()))
                .thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString(), anyString()))
                .thenReturn("refresh_token");

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getUser());

        verify(userFeignClient).authenticateUser("testuser", "password123");
        verify(httpServletResponse, times(2)).addCookie(any(Cookie.class));
        verify(httpServletResponse).setHeader("X-Access-Token", "access_token");
        verify(httpServletResponse).setHeader("X-Refresh-Token", "refresh_token");
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("wrongpassword");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        AuthenticateUserRequest authRequest = new AuthenticateUserRequest("testuser", "wrongpassword");
        Response nullResponse = new Response();
        nullResponse.setUser(null);
        when(userFeignClient.authenticateUser("testuser", "wrongpassword")).thenReturn(nullResponse);

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getMessage().contains("Invalid credentials"));
    }

    @Test
    void testLogin_PendingAccount() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        mockUser.setStatus("pending");
        Response pendingResponse = new Response();
        pendingResponse.setUser(mockUser);
        AuthenticateUserRequest authRequest = new AuthenticateUserRequest("testuser", "password123");
        when(userFeignClient.authenticateUser("testuser", "password123")).thenReturn(pendingResponse);

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(403, response.getStatusCode());
        assertTrue(response.getMessage().contains("Account not verified"));
    }

    @Test
    void testLogin_BannedAccount() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        mockUser.setStatus("banned");
        Response bannedResponse = new Response();
        bannedResponse.setUser(mockUser);
        AuthenticateUserRequest authRequest = new AuthenticateUserRequest("testuser", "password123");
        when(userFeignClient.authenticateUser("testuser", "password123")).thenReturn(bannedResponse);

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(405, response.getStatusCode());
        assertTrue(response.getMessage().contains("Account is banned"));
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullname("New User");
        String dataJson = objectMapper.writeValueAsString(registerRequest);

        UserCreateRequest createRequest = new UserCreateRequest("newuser", "newuser@example.com", "password123", "New User");
        Response createResponse = new Response();
        createResponse.setUser(mockUser);
        when(userFeignClient.createUser(dataJson)).thenReturn(createResponse);

        // Act
        Response response = authService.register(dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Registration successful", response.getMessage());
        assertNotNull(response.getUser());

        verify(userFeignClient).createUser(dataJson);
    }

    @Test
    void testValidateToken_Valid() {
        // Arrange
        String token = "valid_token";
        String username = "testuser";
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        // Act
        Response response = authService.validateToken(token, username);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Token validation successful", response.getMessage());
        assertTrue((Boolean) response.getAdditionalData().get("valid"));

        verify(jwtUtil).validateToken(token, username);
    }

    @Test
    void testValidateToken_Invalid() {
        // Arrange
        String token = "invalid_token";
        String username = "testuser";
        when(jwtUtil.validateToken(token, username)).thenReturn(false);

        // Act
        Response response = authService.validateToken(token, username);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertFalse((Boolean) response.getAdditionalData().get("valid"));
    }

    @Test
    void testSendOTP_Success() {
        // Arrange
        String identifier = "test@example.com";
        String otp = "123456";

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);
        when(otpService.generateOtp(anyString())).thenReturn(otp);

        // Act
        Response response = authService.sendOTP(identifier);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("OTP is sent!", response.getMessage());

        verify(userFeignClient).findUserByIdentifier(identifier);
        verify(otpService).generateOtp(mockUser.getEmail());
        verify(authProducer).sendMailActivation(mockUser.getEmail(), otp);
    }

    @Test
    void testSendOTP_UserNotFound() {
        // Arrange
        String identifier = "nonexistent@example.com";
        Response nullResponse = new Response();
        nullResponse.setUser(null);
        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(nullResponse);

        // Act
        Response response = authService.sendOTP(identifier);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getMessage().contains("User not found"));
    }

    @Test
    void testVerifyOTP_Success() throws Exception {
        // Arrange
        String identifier = "test@example.com";
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtp("123456");
        verifyRequest.setIsActivation(true);
        String dataJson = objectMapper.writeValueAsString(verifyRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);

        // Act
        Response response = authService.verifyOTP(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Otp verified successfully!", response.getMessage());

        verify(otpService).validateOtp(mockUser.getEmail(), "123456");
        verify(userFeignClient).activateUser(mockUser.getEmail());
    }

    @Test
    void testVerifyOTP_InvalidOTP() throws Exception {
        // Arrange
        String identifier = "test@example.com";
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtp("wrong_otp");
        String dataJson = objectMapper.writeValueAsString(verifyRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(false);

        // Act
        Response response = authService.verifyOTP(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Arrange
        String identifier = "testuser";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(changePasswordRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);
        Response changeResponse = new Response();
        changeResponse.setUser(mockUser);
        when(userFeignClient.changePassword(eq(identifier), anyString())).thenReturn(changeResponse);

        // Act
        Response response = authService.changePassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Password changed successfully!", response.getMessage());

        verify(userFeignClient).findUserByIdentifier(identifier);
        verify(userFeignClient).changePassword(eq(identifier), anyString());
    }

    @Test
    void testChangePassword_PasswordMismatch() throws Exception {
        // Arrange
        String identifier = "testuser";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("differentPassword");
        String dataJson = objectMapper.writeValueAsString(changePasswordRequest);

        lenient().when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.changePassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";

        Response resetResponse = new Response();
        resetResponse.setAdditionalData(Map.of("newPassword", newPassword));
        when(userFeignClient.resetPassword(email)).thenReturn(resetResponse);

        // Act
        Response response = authService.resetPassword(email);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Password reset email sent successfully!", response.getMessage());

        verify(userFeignClient).resetPassword(email);
        verify(authProducer).sendMailResetPassword(email, newPassword);
    }

    @Test
    void testRefreshToken_Success() {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid_refresh_token");

        when(jwtUtil.validateRefreshToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("test@example.com");
        when(jwtUtil.extractUserId(anyString())).thenReturn("1");
        when(userFeignClient.findUserByEmail(anyString())).thenReturn(mockUserResponse);
        when(jwtUtil.generateAccessToken(anyString(), anyString(), anyString(), anyString()))
                .thenReturn("new_access_token");

        // Act
        Response response = authService.refreshToken(
                refreshTokenRequest, null, httpServletRequest, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Token refreshed successfully!", response.getMessage());

        verify(httpServletResponse).addCookie(any(Cookie.class));
        verify(httpServletResponse).setHeader("X-Access-Token", "new_access_token");
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("invalid_refresh_token");

        when(jwtUtil.validateRefreshToken(anyString())).thenReturn(false);

        // Act
        Response response = authService.refreshToken(
                refreshTokenRequest, null, httpServletRequest, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCode());
        assertTrue(response.getMessage().contains("Invalid or expired refresh token"));
    }

    @Test
    void testLogout_Success() {
        // Act
        Response response = authService.logout(httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Logged out successfully", response.getMessage());

        verify(httpServletResponse, times(2)).addCookie(any(Cookie.class));
        verify(httpServletResponse).setHeader("X-Access-Token", "");
        verify(httpServletResponse).setHeader("X-Refresh-Token", "");
    }

    // Additional test cases for forgotPassword (completely missing)
    @Test
    void testForgotPassword_Success() throws Exception {
        // Arrange
        String identifier = "testuser";
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setPassword("newPassword");
        forgotRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(forgotRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.forgotPassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Password updated successfully!", response.getMessage());

        verify(userFeignClient).forgotPassword(eq(mockUser.getEmail()), anyString());
    }

    @Test
    void testForgotPassword_UserNotFound() throws Exception {
        // Arrange
        String identifier = "nonexistent";
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setPassword("newPassword");
        forgotRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(forgotRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(null);

        // Act
        Response response = authService.forgotPassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getMessage().contains("User not found"));
    }

    @Test
    void testForgotPassword_PasswordMismatch() throws Exception {
        // Arrange
        String identifier = "testuser";
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setPassword("newPassword");
        forgotRequest.setConfirmPassword("differentPassword");
        String dataJson = objectMapper.writeValueAsString(forgotRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.forgotPassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Password does not match"));
    }

    @Test
    void testForgotPassword_NullFields() throws Exception {
        // Arrange
        String identifier = "testuser";
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setPassword(null);
        forgotRequest.setConfirmPassword("");
        String dataJson = objectMapper.writeValueAsString(forgotRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.forgotPassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode()); // NullPointerException in service
    }

    // Additional test cases for register
    @Test
    void testRegister_DuplicateEmail() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullname("New User");
        String dataJson = objectMapper.writeValueAsString(registerRequest);

        when(userFeignClient.createUser(any(String.class)))
                .thenThrow(new OurException("Email already exists", 409));

        // Act
        Response response = authService.register(dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(409, response.getStatusCode());
        assertTrue(response.getMessage().contains("Email already exists"));
    }

    @Test
    void testRegister_EmptyFields() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setEmail(null);
        registerRequest.setPassword("password123");
        registerRequest.setFullname("New User");
        String dataJson = objectMapper.writeValueAsString(registerRequest);

        // Act
        Response response = authService.register(dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode()); // Service does not validate empty fields
    }

    @Test
    void testRegister_InvalidJson() throws Exception {
        // Arrange
        String dataJson = "{invalid json";

        // Act
        Response response = authService.register(dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    // Additional test cases for login
    @Test
    void testLogin_NullIdentifier() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier(null);
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode()); // User not found when identifier is null
    }

    @Test
    void testLogin_InvalidJson() throws Exception {
        // Arrange
        String dataJson = "{invalid";

        // Act
        Response response = authService.login(dataJson, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    // Additional test cases for verifyOTP
    @Test
    void testVerifyOTP_NullIdentifier() throws Exception {
        // Arrange
        String identifier = null;
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtp("123456");
        String dataJson = objectMapper.writeValueAsString(verifyRequest);

        // Act
        Response response = authService.verifyOTP(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testVerifyOTP_UserNotFound() throws Exception {
        // Arrange
        String identifier = "nonexistent";
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtp("123456");
        String dataJson = objectMapper.writeValueAsString(verifyRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(null);

        // Act
        Response response = authService.verifyOTP(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testVerifyOTP_InvalidJson() throws Exception {
        // Arrange
        String identifier = "test@example.com";
        String dataJson = "{invalid";

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.verifyOTP(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    // Additional test cases for changePassword
    @Test
    void testChangePassword_UserNotFound() throws Exception {
        // Arrange
        String identifier = "nonexistent";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(changePasswordRequest);

        when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(null);

        // Act
        Response response = authService.changePassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testChangePassword_NullFields() throws Exception {
        // Arrange
        String identifier = "testuser";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword(null);
        changePasswordRequest.setNewPassword("");
        changePasswordRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(changePasswordRequest);

        lenient().when(userFeignClient.findUserByIdentifier(identifier)).thenReturn(mockUserResponse);

        // Act
        Response response = authService.changePassword(identifier, dataJson);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    // Additional test cases for resetPassword
    @Test
    void testResetPassword_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userFeignClient.resetPassword(email)).thenThrow(new OurException("User not found", 404));

        // Act
        Response response = authService.resetPassword(email);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testResetPassword_NullEmail() {
        // Arrange
        String email = null;

        // Act
        Response response = authService.resetPassword(email);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode()); // Service now validates null email
    }

    // Additional test cases for refreshToken
    @Test
    void testRefreshToken_NoToken() {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(null);

        // Act
        Response response = authService.refreshToken(
                refreshTokenRequest, null, httpServletRequest, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testRefreshToken_UserNotFound() {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid_refresh_token");

        when(jwtUtil.validateRefreshToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("nonexistent@example.com");
        when(jwtUtil.extractUserId(anyString())).thenReturn("some-user-id");
        when(userFeignClient.findUserByEmail(anyString())).thenReturn(null);

        // Act
        Response response = authService.refreshToken(
                refreshTokenRequest, null, httpServletRequest, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }
}
