package com.example.authservice.controller;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.service.AuthService;
import com.example.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody AuthRequest authRequest) {
        Response response = authService.authenticate(authRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticateUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            Response response = new Response(400, "Username and password are required");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        // Create AuthRequest from the credentials map
        AuthRequest authRequest = new AuthRequest(username, password);
        Response response = authService.authenticate(authRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest registerRequest) {
        Response response = authService.registerUser(registerRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Response> validateToken(@RequestParam String token, @RequestParam String username) {
        Response response = authService.validateToken(token, username);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "Auth Service is running");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // OAuth2 test endpoint để kiểm tra routing
    @GetMapping("/oauth2/test")
    public ResponseEntity<Response> oauth2Test() {
        Response response = new Response(200, "OAuth2 routing is working! Ready for OAuth2 implementation.");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}