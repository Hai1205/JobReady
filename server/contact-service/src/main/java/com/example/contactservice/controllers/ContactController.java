package com.example.contactservice.controllers;

import com.example.contactservice.dtos.response.Response;
import com.example.contactservice.services.apis.ContactApi;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    @Autowired
    private ContactApi contactApi;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> submitContact(@RequestPart("data") String dataJson) {
        Response response = contactApi.submitContact(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> getAllContacts() {
        Response response = contactApi.getAllContacts();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{contactId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> getContactById(@PathVariable("contactId") UUID contactId) {
        Response response = contactApi.getContactById(contactId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{contactId}/resolve")
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<Response> resolveContact(
            @PathVariable("contactId") UUID contactId) {
        Response response = contactApi.resolveContact(contactId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> deleteContact(@PathVariable("contactId") UUID contactId) {
        Response response = contactApi.deleteContact(contactId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("User Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}