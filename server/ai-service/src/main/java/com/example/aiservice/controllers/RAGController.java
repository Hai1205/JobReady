package com.example.aiservice.controllers;

import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.DocumentService;
import com.example.aiservice.services.RAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@Slf4j
public class RAGController {

    private final DocumentService documentService;
    private final RAGService ragService;

    /**
     * Upload và xử lý tài liệu
     */
    @PostMapping("/documents/upload")
    public ResponseEntity<Response> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            if (title != null) metadata.put("title", title);
            if (category != null) metadata.put("category", category);
            
            documentService.processAndStoreDocument(file, userId, metadata);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Document uploaded and processed successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Upload text content trực tiếp
     */
    @PostMapping("/documents/text")
    public ResponseEntity<Response> uploadText(
            @RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            String userId = (String) request.get("userId");
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", new HashMap<>());
            
            documentService.processAndStoreText(text, userId, metadata);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Text processed and stored successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error processing text", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * RAG Query - Hỏi đáp dựa trên tài liệu đã upload
     */
    @PostMapping("/query")
    public ResponseEntity<Response> query(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            String userId = request.get("userId");
            
            String answer = ragService.query(question, userId);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Query successful")
                    .additionalData(Map.of("answer", answer))
                    .build());
        } catch (Exception e) {
            log.error("Error processing query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * RAG Query với system prompt tùy chỉnh
     */
    @PostMapping("/query/custom")
    public ResponseEntity<Response> queryWithSystemPrompt(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            String userId = request.get("userId");
            String systemPrompt = request.get("systemPrompt");
            
            String answer = ragService.queryWithSystemPrompt(question, userId, systemPrompt);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Query successful")
                    .additionalData(Map.of("answer", answer))
                    .build());
        } catch (Exception e) {
            log.error("Error processing custom query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy danh sách documents liên quan (không generate answer)
     */
    @PostMapping("/search")
    public ResponseEntity<Response> searchDocuments(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            String userId = request.get("userId");
            
            List<Map<String, Object>> documents = ragService.getRelevantDocuments(query, userId);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Search successful")
                    .additionalData(Map.of(
                            "documents", documents,
                            "count", documents.size()
                    ))
                    .build());
        } catch (Exception e) {
            log.error("Error searching documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Xóa tất cả documents của user
     */
    @DeleteMapping("/documents/{userId}")
    public ResponseEntity<Response> deleteUserDocuments(@PathVariable String userId) {
        try {
            documentService.deleteDocumentsByUser(userId);
            
            return ResponseEntity.ok(Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("User documents deleted successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting user documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }
}
