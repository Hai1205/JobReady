package com.example.aiservice.services;

import com.example.aiservice.entities.CVTemplateEntity;
import com.example.aiservice.repositories.CVTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CVKnowledgeBaseServiceTest {

    @Mock
    private CVTemplateRepository templateRepository;

    @Mock
    private EnhancedEmbeddingService embeddingService;

    @InjectMocks
    private CVKnowledgeBaseService knowledgeBaseService;

    private CVTemplateEntity testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = CVTemplateEntity.builder()
            .id("test-id")
            .category("tech")
            .level("senior")
            .section("summary")
            .content("Test content")
            .rating(5)
            .isActive(true)
            .build();
    }

    @Test
    void testGetAllTemplates() {
        // Arrange
        List<CVTemplateEntity> templates = Arrays.asList(testTemplate);
        when(templateRepository.findByIsActiveTrue()).thenReturn(templates);

        // Act
        List<CVTemplateEntity> result = knowledgeBaseService.getAllTemplates();

        // Assert
        assertEquals(1, result.size());
        assertEquals("test-id", result.get(0).getId());
        verify(templateRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testAddTemplate() {
        // Arrange
        when(templateRepository.save(any())).thenReturn(testTemplate);
        doNothing().when(embeddingService).ingestTemplate(anyString(), anyString(), any());

        // Act
        CVTemplateEntity result = knowledgeBaseService.addTemplate(testTemplate);

        // Assert
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        verify(templateRepository, times(1)).save(any());
        verify(embeddingService, times(1)).ingestTemplate(anyString(), anyString(), any());
    }

    @Test
    void testAddTemplate_LowRating_NotIngested() {
        // Arrange
        testTemplate.setRating(3); // Low rating
        when(templateRepository.save(any())).thenReturn(testTemplate);

        // Act
        CVTemplateEntity result = knowledgeBaseService.addTemplate(testTemplate);

        // Assert
        assertNotNull(result);
        verify(embeddingService, never()).ingestTemplate(anyString(), anyString(), any());
    }

    @Test
    void testUpdateTemplate() {
        // Arrange
        CVTemplateEntity existingTemplate = CVTemplateEntity.builder()
            .id("test-id")
            .category("tech")
            .level("mid")
            .section("experience")
            .content("Old content")
            .rating(4)
            .isActive(true)
            .build();

        CVTemplateEntity updatedData = CVTemplateEntity.builder()
            .content("New content")
            .category("tech")
            .level("senior")
            .section("experience")
            .rating(5)
            .build();

        when(templateRepository.findById("test-id")).thenReturn(Optional.of(existingTemplate));
        when(templateRepository.save(any())).thenReturn(existingTemplate);
        doNothing().when(embeddingService).updateTemplate(anyString(), anyString(), any());

        // Act
        CVTemplateEntity result = knowledgeBaseService.updateTemplate("test-id", updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("New content", result.getContent());
        verify(embeddingService, times(1)).updateTemplate(anyString(), anyString(), any());
    }

    @Test
    void testDeleteTemplate() {
        // Arrange
        when(templateRepository.findById("test-id")).thenReturn(Optional.of(testTemplate));
        when(templateRepository.save(any())).thenReturn(testTemplate);
        doNothing().when(embeddingService).deleteTemplate(anyString());

        // Act
        knowledgeBaseService.deleteTemplate("test-id");

        // Assert
        verify(templateRepository, times(1)).save(any());
        verify(embeddingService, times(1)).deleteTemplate("test-id");
    }

    @Test
    void testDeleteTemplate_NotFound() {
        // Arrange
        when(templateRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            knowledgeBaseService.deleteTemplate("non-existent");
        });
    }
}