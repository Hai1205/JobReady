package com.example.cvservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.services.apis.CVApi;

@RestController
@RequestMapping("/api/v1/cvs")
public class CVController {

    @Autowired
    private CVApi cvService;

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
    public ResponseEntity<Response> updateCV(
            @PathVariable("cvId") UUID cvId,
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

    @GetMapping("/stats/total")
    // @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getTotalCVs() {
        Response response = cvService.getTotalCVs();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/import/users/{userId}")
    // @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> importCV(
        @PathVariable("userId") UUID userId, 
        @RequestBody String dataJson) {
        Response response = cvService.importCV(userId, dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/stats/visibility/{visibility}")
    // @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getCVsByVisibility(@PathVariable("visibility") boolean isVisibility) {
        Response response = cvService.getCVsByVisibility(isVisibility);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/stats/created-range")
    // @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getCVsCreatedInRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        Response response = cvService.getCVsCreatedInRange(startDate, endDate);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/recent")
    // @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getRecentCVs(@RequestParam("limit") int limit) {
        Response response = cvService.getRecentCVs(limit);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}