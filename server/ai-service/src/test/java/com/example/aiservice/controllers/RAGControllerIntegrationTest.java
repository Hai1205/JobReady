package com.example.aiservice.controllers;

import com.example.aiservice.services.CVKnowledgeBaseService;
import com.example.aiservice.services.RAGService;
import com.example.aiservice.services.DocumentService;
import com.example.aiservice.entities.CVTemplateEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class cho RAG endpoints
 */
@WebMvcTest(RAGController.class)
@EnableAutoConfiguration(exclude = {
    org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions",
    "OPENROUTER_API_KEY=test-key",
    "OPENROUTER_API_MODEL=gpt-3.5-turbo"
})
public class RAGControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private RAGService ragService;

    @MockBean
    private CVKnowledgeBaseService knowledgeBaseService;

    @Test
    public void testUploadDocument() throws Exception {
        // Mock service method
        doNothing().when(documentService).processAndStoreDocument(any(), anyString(), any());

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "This is a test document content".getBytes()
        );

        mockMvc.perform(multipart("/api/rag/documents/upload")
                .file(file)
                .param("userId", "testUser")
                .param("title", "Test Document"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testUploadText() throws Exception {
        // Mock service method
        doNothing().when(documentService).processAndStoreText(anyString(), anyString(), any());

        String jsonRequest = """
            {
                "text": "Java is a programming language. Spring Boot is a framework.",
                "userId": "testUser",
                "metadata": {
                    "category": "tech"
                }
            }
            """;

        mockMvc.perform(post("/api/rag/documents/text")
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    public void testQuery() throws Exception {
        // Mock RAG service
        when(ragService.query(anyString(), anyString())).thenReturn("Mocked answer");

        String jsonRequest = """
            {
                "question": "What is Java?",
                "userId": "testUser"
            }
            """;

        mockMvc.perform(post("/api/rag/query")
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.additionalData.answer").exists());
    }

    @Test
    public void testSearchDocuments() throws Exception {
        // Mock RAG service
        when(ragService.getRelevantDocuments(anyString(), anyString()))
            .thenReturn(List.of(Map.of("content", "Mock content")));

        String jsonRequest = """
            {
                "query": "programming",
                "userId": "testUser"
            }
            """;

        mockMvc.perform(post("/api/rag/search")
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.additionalData.documents").exists());
    }

    @Test
    public void testGetAllTemplates() throws Exception {
        // Mock knowledge base service
        List<CVTemplateEntity> templates = new ArrayList<>();
        templates.add(CVTemplateEntity.builder()
            .id("test-id")
            .category("tech")
            .level("senior")
            .section("summary")
            .content("Test content")
            .rating(5)
            .isActive(true)
            .build());
        
        when(knowledgeBaseService.getAllTemplates()).thenReturn(templates);

        mockMvc.perform(get("/api/rag/admin/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.additionalData.templates").isArray())
                .andExpect(jsonPath("$.additionalData.count").value(1));
    }

    @Test
    public void testAddTemplate() throws Exception {
        // Mock knowledge base service
        CVTemplateEntity savedTemplate = CVTemplateEntity.builder()
            .id("new-id")
            .category("tech")
            .level("mid")
            .section("experience")
            .content("New template content")
            .rating(4)
            .isActive(true)
            .build();
        
        when(knowledgeBaseService.addTemplate(any())).thenReturn(savedTemplate);

        String jsonRequest = """
            {
                "category": "tech",
                "level": "mid",
                "section": "experience",
                "content": "New template content",
                "rating": 4
            }
            """;

        mockMvc.perform(post("/api/rag/admin/templates")
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.additionalData.template.id").value("new-id"));
    }

    @Test
    public void testReinitializeKnowledgeBase() throws Exception {
        // Mock knowledge base service
        doNothing().when(knowledgeBaseService).reinitializeKnowledgeBase();

        mockMvc.perform(post("/api/rag/admin/reinitialize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Knowledge base reinitialized successfully"));
    }
}