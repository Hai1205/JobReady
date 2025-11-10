package com.example.cvservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.services.apis.CVService;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/cvs")
public class CVController {

    @Autowired
    private CVService cvService;

    @PostMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> createCV(@PathVariable("userId") UUID userId) {
        Response response = cvService.createCV(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllCVs() {
        Response response = cvService.getAllCVs();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getCVById(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.getCVById(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getUserCVs(@PathVariable("userId") UUID userId) {
        Response response = cvService.getUserCVs(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getCVByTitle(@PathVariable("title") String title) {
        Response response = cvService.getCVByTitle(title);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> updateCV(@PathVariable("cvId") UUID cvId,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        Response response = cvService.updateCV(cvId, dataJson, avatar);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> deleteCV(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.deleteCV(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/{cvId}/duplicate")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> duplicateCV(@PathVariable("cvId") UUID cvId) {
        Response response = cvService.duplicateCV(cvId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
     @GetMapping("/health1")
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<Response> health1() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/health2")
    @PreAuthorize("hasAnyAuthority('user')")
    public ResponseEntity<Response> health2() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/health3")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> health3() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/health4")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Response> health4() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("CV Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}