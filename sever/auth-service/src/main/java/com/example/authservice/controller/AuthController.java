package com.example.authservice.controller;

import com.example.authservice.dto.responses.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response> login(
            @ModelAttribute LoginRequest request,
            HttpServletResponse httpServletResponse) {
        Response response = authService.login(request, httpServletResponse);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @ModelAttribute RegisterRequest request) {
        Response response = authService.register(request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<Response> sendOTP(@PathVariable("email") String email) {
        Response response = authService.sendOTP(email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/verify-otp/{email}")
    public ResponseEntity<Response> verifyOTP(
            @PathVariable("email") String email,
            @ModelAttribute VerifyOtpRequest request) {
        Response response = authService.verifyOTP(email, request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<Response> changePassword(
            @PathVariable("email") String email,
            @ModelAttribute ChangePasswordRequest changePasswordRequest) {
        Response response = authService.changePassword(email,
                changePasswordRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/reset-password/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> resetPassword(
            @PathVariable("email") String email) {
        Response response = authService.resetPassword(email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/forgot-password/{email}")
    public ResponseEntity<Response> forgotPassword(
            @PathVariable("email") String email,
            @ModelAttribute ChangePasswordRequest changePasswordRequest) {
        Response response = authService.forgotPassword(email,
                changePasswordRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Response> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            HttpServletResponse httpServletResponse) {
        Response response = authService.refreshToken(refreshTokenRequest, authorizationHeader, httpServletResponse);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse httpServletResponse) {
        Response response = authService.logout(httpServletResponse);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "Auth Service is running");
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}