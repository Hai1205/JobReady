package com.example.aiservice.controllers;

import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.apis.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for AIController
 * Tests REST endpoints with Spring Security and MockMvc
 */
@WebMvcTest(AIController.class)
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIService aiService;

    private Response mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new Response(200, "Success");
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testAnalyzeCV_Success() throws Exception {
        // Arrange
        String jsonData = "{\"title\":\"Test CV\",\"personalInfo\":{\"fullname\":\"John Doe\"}}";
        when(aiService.analyzeCV(jsonData)).thenReturn(mockResponse);

        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/ai/analyze")
                .file(dataFile)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(aiService).analyzeCV(jsonData);
    }

    @Test
    void testAnalyzeCV_Unauthorized() throws Exception {
        // Arrange
        String jsonData = "{\"title\":\"Test CV\"}";
        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/ai/analyze")
                .file(dataFile)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testImproveCV_Success() throws Exception {
        // Arrange
        String jsonData = "{\"section\":\"summary\",\"content\":\"Old content\"}";
        when(aiService.improveCV(jsonData)).thenReturn(mockResponse);

        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/ai/improve")
                .file(dataFile)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(aiService).improveCV(jsonData);
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testAnalyzeCVWithJobDescription_Success() throws Exception {
        // Arrange
        String jsonData = "{\"title\":\"Test CV\",\"jobDescription\":\"Looking for developer\"}";
        when(aiService.analyzeCVWithJobDescription(eq(jsonData), isNull())).thenReturn(mockResponse);

        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/ai/analyze-with-jd")
                .file(dataFile)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(aiService).analyzeCVWithJobDescription(eq(jsonData), isNull());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testHealth_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("AI Service is running"));
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testInvalidEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/api/invalid"))
                .andExpect(status().isNotFound());
    }
}