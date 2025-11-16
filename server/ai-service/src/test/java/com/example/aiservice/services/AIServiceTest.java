package com.example.aiservice.services;

import com.example.aiservice.configs.OpenRouterConfig;
import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.apis.AIApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private OpenRouterConfig openRouterConfig;

    @Mock
    private PromptBuilderService promptBuilderService;

    @InjectMocks
    private AIApi aiService;

    @Test
    void testAnalyzeCV_NullCV() {
        // Act
        Response response = aiService.analyzeCV(null);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("null"));
    }

    @Test
    void testAnalyzeCV_InvalidJson() {
        // Act
        Response response = aiService.analyzeCV("invalid json");

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Unrecognized token") || response.getMessage().contains("JSON"));
    }
}
