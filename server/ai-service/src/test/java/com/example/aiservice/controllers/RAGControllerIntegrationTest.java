package com.example.aiservice.controllers;

import com.example.aiservice.services.RAGService;
import com.example.aiservice.services.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class cho RAG endpoints
 * Note: Cần có PostgreSQL + pgvector đang chạy để test
 */
@WebMvcTest(RAGController.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
                org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration.class
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
        private EmbeddingModel embeddingModel;

        @MockBean
        private ChatModel chatModel;

        @MockBean
        private VectorStore vectorStore;

        @MockBean
        private com.example.aiservice.configs.OpenRouterConfig openRouterConfig;

        @MockBean
        private DocumentService documentService;

        @MockBean
        private RAGService ragService;

        @Test
        public void testUploadDocument() throws Exception {
                // Mock embedding response
                doReturn(new float[] { 0.1f, 0.2f, 0.3f }).when(embeddingModel).embed(anyString());

                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.txt",
                                "text/plain",
                                "This is a test document content".getBytes());

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
                // Mock embedding response
                doReturn(new float[] { 0.1f, 0.2f, 0.3f }).when(embeddingModel).embed(anyString());

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
                // Mock vector store search
                List<Document> mockDocuments = List.of(new Document("Mock content"));
                doReturn(mockDocuments).when(vectorStore).similaritySearch(any(SearchRequest.class));

                // Mock chat response
                @SuppressWarnings("deprecation")
                ChatResponse mockChatResponse = new ChatResponse(
                                List.of(new Generation(new AssistantMessage("Mocked answer"))));
                when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

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
                // Mock vector store search
                List<Document> mockDocuments = List.of(new Document("Mock content"));
                doReturn(mockDocuments).when(vectorStore).similaritySearch(any(SearchRequest.class));

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
}
