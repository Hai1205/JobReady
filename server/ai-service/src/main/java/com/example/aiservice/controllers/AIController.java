package com.example.aiservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.services.apis.AIApi;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    @Autowired
    private AIApi aiService;

    @PostMapping("/analyze")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> analyzeCV(@RequestPart("data") String dataJson) {
        Response response = aiService.analyzeCV(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/improve")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> improveCV(@RequestPart("data") String dataJson) {
        Response response = aiService.improveCV(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/analyze-with-jd")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> analyzeCVWithJobDescription(@RequestPart("data") String dataJson,
            @RequestPart(value = "jdFile", required = false) MultipartFile jdFile) {
        Response response = aiService.analyzeCVWithJobDescription(dataJson, jdFile);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/users/{userId}/import")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> importCV(
            @PathVariable("userId") UUID userId,
            @RequestPart(value = "file", required = true) MultipartFile file) {
        Response response = aiService.importCV(userId, file);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("AI Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}