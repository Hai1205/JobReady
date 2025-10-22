package com.example.userservice.controllers;

import com.example.userservice.dtos.response.Response;
import com.example.userservice.services.apis.UserService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> createUser(@RequestPart("data") String dataJson) {
        Response response = userService.createUser(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") UUID userId) {
        Response response = userService.getUserById(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<Response> updateUser(
            @PathVariable("userId") UUID userId,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        Response response = userService.updateUser(userId, dataJson, avatar);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") UUID userId) {
        Response response = userService.deleteUser(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("User Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}