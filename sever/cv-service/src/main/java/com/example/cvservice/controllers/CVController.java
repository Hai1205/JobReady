package com.example.cvservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.dtos.requests.*;
import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.services.apis.CVService;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/cvs")
public class CVController {

    @Autowired
    private CVService cvService;

    @PostMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> createCV(
            @PathVariable("userId") UUID userId,
            @ModelAttribute CreateCVRequest request) {
        Response response = cvService.createCV(userId, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllCVs() {
        Response response = cvService.getAllCVs();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> getCVById(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.getCVById(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/analyze/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> analyzeCV(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.analyzeCV(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/improve/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> improveCV(@PathVariable("cvId") UUID cvId,
            @ModelAttribute ImproveCVRequest request) {
        Response response = cvService.improveCV(cvId, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/analyze-with-jd/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> analyzeCVWithJobDescription(
            @PathVariable("cvId") UUID cvId,
            @ModelAttribute AnalyzeCVWithJDRequest request) {
        Response response = cvService.analyzeCVWithJobDescription(cvId, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> getUserCVs(@PathVariable("userId") UUID userId) {
        Response response = cvService.getUserCVs(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/users/{userId}/import")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> importFile(
            @PathVariable("userId") UUID userId,
            @RequestParam("file") MultipartFile file) {
        Response response = cvService.importFile(userId, file);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/tittle/{tittle}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> getCVByTitle(@PathVariable("tittle") String tittle) {
        Response response = cvService.getCVByTitle(tittle);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> updateCV(@PathVariable("cvId") UUID cvId, @ModelAttribute UpdateCVRequest request) {
        Response response = cvService.updateCV(cvId, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> deleteCV(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.deleteCV(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    @PreAuthorize("hasAuthority('ADMINA')")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}