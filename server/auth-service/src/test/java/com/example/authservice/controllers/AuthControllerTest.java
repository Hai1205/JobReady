package com.example.authservice.controllers;

import com.example.authservice.controllers.AuthController;
import com.example.authservice.dtos.UserDto;
import com.example.authservice.dtos.requests.*;
import com.example.authservice.dtos.responses.Response;
import com.example.authservice.services.apis.AuthApi;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthApi authService;

    @MockBean
    private com.example.securitycommon.jwts.JwtTokenProvider tokenProvider;

    @MockBean
    private com.example.securitycommon.handlers.JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @MockBean
    private com.example.securitycommon.handlers.JsonAccessDeniedHandler jsonAccessDeniedHandler;

    @MockBean
    private com.example.rediscommon.services.RedisService redisService;

    @MockBean
    private org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;

    private ObjectMapper objectMapper;
    private Response successResponse;
    private UserDto mockUser;

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

        // Mock success response
        successResponse = new Response();
        successResponse.setStatusCode(200);
        successResponse.setMessage("Success");
        successResponse.setUser(mockUser);
    }

    @Test
    @WithMockUser
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        when(authService.login(anyString(), any(HttpServletResponse.class)))
                .thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/auth/login")
                .file(dataPart)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(authService).login(anyString(), any(HttpServletResponse.class));
    }

    @Test
    @WithMockUser
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullname("New User");
        String dataJson = objectMapper.writeValueAsString(registerRequest);

        when(authService.register(anyString())).thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/auth/register")
                .file(dataPart)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).register(anyString());
    }

    @Test
    @WithMockUser
    void testSendOTP_Success() throws Exception {
        // Arrange
        when(authService.sendOTP(anyString())).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/send-otp/test@example.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).sendOTP("test@example.com");
    }

    @Test
    @WithMockUser
    void testVerifyOTP_Success() throws Exception {
        // Arrange
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtp("123456");
        String dataJson = objectMapper.writeValueAsString(verifyRequest);

        when(authService.verifyOTP(anyString(), anyString())).thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/auth/verify-otp/test@example.com")
                .file(dataPart)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).verifyOTP(anyString(), anyString());
    }

    @Test
    @WithMockUser
    void testChangePassword_Success() throws Exception {
        // Arrange
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(changePasswordRequest);

        when(authService.changePassword(anyString(), anyString())).thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/auth/change-password/testuser")
                .file(dataPart)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).changePassword(anyString(), anyString());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testResetPassword_Success() throws Exception {
        // Arrange
        when(authService.resetPassword(anyString())).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/auth/reset-password/test@example.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).resetPassword("test@example.com");
    }

    @Test
    @WithMockUser
    void testForgotPassword_Success() throws Exception {
        // Arrange
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setPassword("newPassword");
        forgotPasswordRequest.setConfirmPassword("newPassword");
        String dataJson = objectMapper.writeValueAsString(forgotPasswordRequest);

        when(authService.forgotPassword(anyString(), anyString())).thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/auth/forgot-password/testuser")
                .file(dataPart)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).forgotPassword(anyString(), anyString());
    }

    @Test
    @WithMockUser
    void testRefreshToken_Success() throws Exception {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid_refresh_token");

        when(authService.refreshToken(any(), any(), any(), any())).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).refreshToken(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void testLogout_Success() throws Exception {
        // Arrange
        when(authService.logout(any(HttpServletResponse.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(authService).logout(any(HttpServletResponse.class));
    }

    @Test
    @WithMockUser
    void testHealth_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/health")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Auth Service is running"));
    }
}
