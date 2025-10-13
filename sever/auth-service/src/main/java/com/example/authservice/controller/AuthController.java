package com.example.authservice.controller;

import com.example.authservice.dto.responses.*;
import com.example.authservice.dto.requests.*;
import com.example.authservice.service.AuthService;

import jakarta.servlet.http.Cookie;
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
            @ModelAttribute LoginRequest loginRequest,
            HttpServletResponse response) {
        Response loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        boolean isActive = loginResponse.getData() != null && loginResponse.getData().getUser() != null
                && loginResponse.getData().getUser().getStatus().equals("ACTIVE");
        boolean isOk = loginResponse.getStatusCode() == 200;

        if (isOk && isActive) {
            int SevenDays = 7 * 24 * 60 * 60;
            Cookie jwtCookie = new Cookie("JWT_TOKEN", loginResponse.getData().getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(SevenDays);

            response.addCookie(jwtCookie);
            response.setHeader("X-JWT-TOKEN", loginResponse.getData().getToken());
        }

        return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @ModelAttribute RegisterRequest registerRequest) {
        Response response = authService.register(registerRequest.getEmail(), registerRequest.getPassword());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<Response> sendOTP(@PathVariable("email") String email) {
        Response response = authService.sendOTP(email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/verify-otp/{email}")
    public ResponseEntity<Response> verifyOTP(
            @PathVariable String email,
            @ModelAttribute OtpVerifyRequest otpRequest) {
        Response response = authService.verifyOTP(email, otpRequest.getOtp());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Response> changePassword(
            @PathVariable String userId,
            @ModelAttribute com.example.authservice.dto.requests.ChangePasswordRequest changePasswordRequest) {
        Response response = authService.changePassword(userId,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getRePassword());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> resetPassword(
            @PathVariable String userId) {
        Response response = authService.resetPassword(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/forgot-password/{email}")
    public ResponseEntity<Response> forgotPassword(
            @PathVariable String email,
            @ModelAttribute ForgotPasswordRequest forgotPasswordRequest) {
        Response response = authService.forgotPassword(email,
                forgotPasswordRequest.getNewPassword(),
                forgotPasswordRequest.getRePassword());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("JWT_TOKEN", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            Response responseLogout = new Response(200, "logged out successfully");
            return ResponseEntity.status(responseLogout.getStatusCode()).body(responseLogout);
        } catch (Exception e) {
            Response errorResponse = new Response(500, "Logout failed: " + e.getMessage());
            return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Response> validateToken(@ModelAttribute TokenValidationRequest validationRequest) {
        Response response = authService.validateToken(validationRequest.getToken(), validationRequest.getUsername());
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