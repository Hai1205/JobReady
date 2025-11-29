package com.example.aiservice.services;

import com.example.aiservice.configs.OpenRouterConfig;
import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.apis.AIApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private OpenRouterConfig openRouterConfig;

    @Mock
    private EnhancedEmbeddingService embeddingService;

    @Mock
    private PromptBuilderService promptBuilderService;

    @Mock
    private FileParserService fileParserService;

    @Mock
    private com.example.aiservice.services.feigns.CVFeignClient cvFeignClient;

    private AIApi aiService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aiService = new AIApi(
            openRouterConfig,
            embeddingService,
            promptBuilderService,
            fileParserService,
            cvFeignClient
        );
    }

    @Test
    void testAnalyzeCV_NullInput() {
        // Act
        Response response = aiService.analyzeCV(null);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getMessage());
    }

    @Test
    void testAnalyzeCV_InvalidJson() {
        // Act
        Response response = aiService.analyzeCV("invalid json");

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Unrecognized token") || 
                   response.getMessage().contains("JSON") ||
                   response.getMessage().contains("Cannot deserialize"));
    }

    @Test
    void testImproveCV_NullInput() {
        // Act
        Response response = aiService.improveCV(null);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getMessage());
    }

    @Test
    void testImproveCV_InvalidJson() {
        // Act
        Response response = aiService.improveCV("not a valid json");

        // Assert
        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getMessage());
    }

    @Test
    void testAnalyzeCVWithJobDescription_NullInput() {
        // Act
        Response response = aiService.analyzeCVWithJobDescription(null, null);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getMessage());
    }
}