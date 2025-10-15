package com.example.userservice.controller;

import com.example.userservice.dto.requests.CreateUserRequest;
import com.example.userservice.dto.requests.UpdateUserRequest;
import com.example.userservice.dto.response.Response;
import com.example.userservice.service.UserService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Response> createUser(@ModelAttribute CreateUserRequest request) {
        Response response = userService.createUser(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable UUID id) {
        Response response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable UUID id, @ModelAttribute UpdateUserRequest request) {
        Response response = userService.updateUser(id, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable UUID id) {
        Response response = userService.deleteUser(id);
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