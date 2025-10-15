package com.example.cvservice.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.dto.requests.*;
import com.example.cvservice.dto.responses.Response;
import com.example.cvservice.service.CVService;

@RestController
@RequestMapping("/cvs")
public class CVController {

    @Autowired
    private CVService userService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<Response> createCV(@PathVariable UUID userId, @ModelAttribute CreateCVRequest request) {
        Response response = userService.createCV(userId, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllCVs() {
        Response response = userService.getAllCVs();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<Response> getCVById(@PathVariable UUID cvId) {
        Response response = userService.getCVById(cvId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/analyze/{cvId}")
    public ResponseEntity<Response> analyseCV(@PathVariable UUID cvId) {
        Response response = userService.analyseCV(cvId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/improve/{cvId}")
    public ResponseEntity<Response> improveCV(@PathVariable UUID cvId, @ModelAttribute ImproveCVRequest request) {
        Response response = userService.improveCV(cvId, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Response> getUserCVs(@PathVariable UUID userId) {
        Response response = userService.getUserCVs(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @PostMapping("/users/{userId}/import")
    public ResponseEntity<Response> importFile(@PathVariable UUID userId, @ModelAttribute MultipartFile file) {
        Response response = userService.importFile(userId, file);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Response> getCVByTitle(@PathVariable String title) {
        Response response = userService.getCVByTitle(title);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{cvId}")
    public ResponseEntity<Response> updateCV(@PathVariable UUID cvId, @ModelAttribute UpdateCVRequest request) {
        Response response = userService.updateCV(cvId, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<Response> deleteCV(@PathVariable UUID cvId) {
        Response response = userService.deleteCV(cvId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "CV Service is running");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}