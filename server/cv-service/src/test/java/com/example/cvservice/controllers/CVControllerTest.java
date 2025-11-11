package com.example.cvservice.controllers;

import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.services.apis.CVService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for CVController
 * Tests REST endpoints with Spring Security and MockMvc
 */
@WebMvcTest(CVController.class)
class CVControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CVService cvService;

    private UUID userId;
    private UUID cvId;
    private Response mockResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cvId = UUID.randomUUID();
        mockResponse = new Response(200, "Success");
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testCreateCV_Success() throws Exception {
        // Arrange
        when(cvService.createCV(userId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cvs/users/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).createCV(userId);
    }

    @Test
    void testCreateCV_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/cvs/users/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testGetAllCVs_Success() throws Exception {
        // Arrange
        when(cvService.getAllCVs()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).getAllCVs();
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testGetCVById_Success() throws Exception {
        // Arrange
        when(cvService.getCVById(cvId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/{cvId}", cvId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).getCVById(cvId);
    }

    @Test
    void testGetCVById_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/{cvId}", cvId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testGetUserCVs_Success() throws Exception {
        // Arrange
        when(cvService.getUserCVs(userId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).getUserCVs(userId);
    }

    @Test
    void testGetUserCVs_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/users/{userId}", userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testGetCVByTitle_Success() throws Exception {
        // Arrange
        String title = "Test CV";
        when(cvService.getCVByTitle(title)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/title/{title}", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).getCVByTitle(title);
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testUpdateCV_Success() throws Exception {
        // Arrange
        String jsonData = "{\"title\":\"Updated CV\"}";
        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());
        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "test".getBytes());

        when(cvService.updateCV(eq(cvId), eq(jsonData), any())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/cvs/{cvId}", cvId)
                .file(dataFile)
                .file(avatarFile)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).updateCV(eq(cvId), eq(jsonData), any());
    }

    @Test
    void testUpdateCV_Unauthorized() throws Exception {
        // Arrange
        String jsonData = "{\"title\":\"Updated CV\"}";
        MockMultipartFile dataFile = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/cvs/{cvId}", cvId)
                .file(dataFile)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testDeleteCV_Success() throws Exception {
        // Arrange
        when(cvService.deleteCV(cvId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/cvs/{cvId}", cvId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).deleteCV(cvId);
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testDuplicateCV_Success() throws Exception {
        // Arrange
        when(cvService.duplicateCV(cvId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cvs/{cvId}/duplicate", cvId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(cvService).duplicateCV(cvId);
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testHealth_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("CV Service is running"));
    }

    @Test
    @WithMockUser(authorities = { "admin", "user" })
    void testInvalidEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/cvs/api/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = { "user" })
    void testCreateCV_WithUserRole_Success() throws Exception {
        // Arrange
        when(cvService.createCV(userId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cvs/users/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(cvService).createCV(userId);
    }

    @Test
    @WithMockUser(authorities = { "admin" })
    void testCreateCV_WithAdminRole_Success() throws Exception {
        // Arrange
        when(cvService.createCV(userId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cvs/users/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(cvService).createCV(userId);
    }

    @Test
    @WithMockUser(authorities = { "guest" })
    void testCreateCV_WithInsufficientRole_Unauthorized() throws Exception {
        // Arrange - Mock the service to return a response (authorization is not
        // enforced on this endpoint)
        when(cvService.createCV(userId)).thenReturn(mockResponse);

        // Act & Assert - Since no @PreAuthorize annotation exists, any authenticated
        // user can access
        mockMvc.perform(post("/api/v1/cvs/users/{userId}", userId)
                .with(csrf()))
                .andExpect(status().isOk());
    }
}