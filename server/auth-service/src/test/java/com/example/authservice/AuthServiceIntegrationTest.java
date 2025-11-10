package com.example.authservice;

import com.example.authservice.dtos.requests.LoginRequest;
import com.example.authservice.dtos.responses.Response;
import com.example.authservice.services.apis.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Auth Service
 * Tests the full application context and API endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Auth Service is running"));
    }

    @Test
    @WithMockUser
    void testLoginEndpoint_Structure() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password123");
        String dataJson = objectMapper.writeValueAsString(loginRequest);

        Response successResponse = new Response(200, "Login successful");

        when(authService.login(anyString(), any())).thenReturn(successResponse);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", dataJson.getBytes());

        // Act & Assert - Testing endpoint structure
        mockMvc.perform(multipart("/api/v1/auth/login")
                .file(dataPart)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testContextLoads() {
        // Test that Spring context loads successfully
    }
}
