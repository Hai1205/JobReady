package com.example.aiservice.controllers;

import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.apis.AIApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for AIController
 * Tests REST endpoints with MockMvc
 */
@WebMvcTest(AIController.class)
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIApi aiService;

    @Test
    @WithMockUser
    void testHealth_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("AI Service is running"));
    }
}
