package com.example.userservice.controllers;

import com.example.userservice.dtos.requests.CreateUserRequest;
import com.example.userservice.dtos.requests.UpdateUserRequest;
import com.example.userservice.dtos.response.Response;
import com.example.userservice.services.apis.UserService;

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
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") UUID userId) {
        Response response = userService.getUserById(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Response> updateUser(@PathVariable("userId") UUID userId, @ModelAttribute UpdateUserRequest request) {
        Response response = userService.updateUser(userId, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") UUID userId) {
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