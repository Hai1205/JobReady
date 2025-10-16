package com.example.userservice.controller;

import com.example.userservice.dto.requests.CreateUserRequest;
import com.example.userservice.dto.requests.UpdateUserRequest;
import com.example.userservice.dto.response.Response;
import com.example.userservice.service.UserService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createUser(@ModelAttribute CreateUserRequest request) {
        Response response = userService.createUser(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable UUID userId) {
        Response response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Response> updateUser(@PathVariable UUID userId, @ModelAttribute UpdateUserRequest request) {
        Response response = userService.updateUser(userId, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable UUID userId) {
        Response response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("User Service is running");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}