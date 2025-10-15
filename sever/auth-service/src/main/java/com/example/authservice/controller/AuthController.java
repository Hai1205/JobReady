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
            @ModelAttribute LoginRequest request,
            HttpServletResponse response) {
        Response loginResponse = authService.login(request);

        boolean isActive = loginResponse.getData() != null && loginResponse.getData().getUser() != null
                && loginResponse.getData().getUser().getStatus().equals("ACTIVE");
        boolean isOk = loginResponse.getStatusCode() == 200;

        if (isOk && isActive) {
            ResponseData data = loginResponse.getData();

            // Set Access Token cookie (15 phút)
            int fifteenMinutes = 15 * 60; // seconds
            Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", data.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set true khi dùng HTTPS
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(fifteenMinutes);
            response.addCookie(accessTokenCookie);

            // Set Refresh Token cookie (7 ngày)
            int sevenDays = 7 * 24 * 60 * 60; // seconds
            Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", data.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set true khi dùng HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(sevenDays);
            response.addCookie(refreshTokenCookie);

            // Set JWT_TOKEN cookie (backward compatibility - sử dụng access token)
            Cookie jwtCookie = new Cookie("JWT_TOKEN", data.getAccessToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(fifteenMinutes);
            response.addCookie(jwtCookie);

            // Set headers
            response.setHeader("X-Access-Token", data.getAccessToken());
            response.setHeader("X-Refresh-Token", data.getRefreshToken());

            // Clear tokens từ response body để tăng security (optional)
            // Uncomment nếu muốn chỉ trả tokens qua cookies/headers
            // data.setToken("");
            // data.setAccessToken("");
            // data.setRefreshToken("");
        }

        return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @ModelAttribute RegisterRequest request) {
        Response response = authService.register(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<Response> sendOTP(@PathVariable String email) {
        Response response = authService.sendOTP(email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/verify-otp/{email}")
    public ResponseEntity<Response> verifyOTP(
            @PathVariable String email,
            @ModelAttribute String otp) {
        Response response = authService.verifyOTP(email, otp);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<Response> changePassword(
            @PathVariable String email,
            @ModelAttribute ChangePasswordRequest changePasswordRequest) {
        Response response = authService.changePassword(email,
                changePasswordRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/reset-password/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> resetPassword(
            @PathVariable String email) {
        Response response = authService.resetPassword(email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/forgot-password/{email}")
    public ResponseEntity<Response> forgotPassword(
            @PathVariable String email,
            @ModelAttribute ChangePasswordRequest changePasswordRequest) {
        Response response = authService.forgotPassword(email,
                changePasswordRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Refresh Token Endpoint
     * Nhận refresh token từ client và trả về access token + refresh token mới
     * 
     * Flow:
     * 1. Client gửi refresh token (từ header hoặc body)
     * 2. Server validate refresh token
     * 3. Nếu hợp lệ: tạo access token mới + refresh token mới
     * 4. Nếu không hợp lệ: trả về 401 Unauthorized
     * 
     * @param refreshTokenRequest Request body chứa refresh token
     * @param authorizationHeader Authorization header (alternative way)
     * @param response            HTTP response để set cookie
     * @return Response chứa access token và refresh token mới
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Response> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            HttpServletResponse response) {

        // Lấy refresh token từ body hoặc header
        String refreshToken = null;

        // Priority 1: Từ request body
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null) {
            refreshToken = refreshTokenRequest.getRefreshToken();
        }
        // Priority 2: Từ Authorization header (format: "Bearer <token>")
        else if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            refreshToken = authorizationHeader.substring(7);
        }

        // Validate refresh token có tồn tại
        if (refreshToken == null || refreshToken.isEmpty()) {
            Response errorResponse = new Response(400, "Refresh token is required");
            return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
        }

        // Gọi service để refresh token
        Response refreshResponse = authService.refreshToken(refreshToken);

        // Nếu refresh thành công, set cookies cho access token và refresh token mới
        if (refreshResponse.getStatusCode() == 200 && refreshResponse.getData() != null) {
            ResponseData data = refreshResponse.getData();

            // Set Access Token cookie (15 phút)
            int fifteenMinutes = 15 * 60; // seconds
            Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", data.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set true nếu dùng HTTPS
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(fifteenMinutes);
            response.addCookie(accessTokenCookie);

            // Set Refresh Token cookie (7 ngày)
            int sevenDays = 7 * 24 * 60 * 60; // seconds
            Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", data.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set true nếu dùng HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(sevenDays);
            response.addCookie(refreshTokenCookie);

            // Set headers (để client có thể lấy token từ response headers)
            response.setHeader("X-Access-Token", data.getAccessToken());
            response.setHeader("X-Refresh-Token", data.getRefreshToken());

            // Clear tokens từ response body để tăng security (optional)
            // data.setAccessToken("");
            // data.setRefreshToken("");
        }

        return ResponseEntity.status(refreshResponse.getStatusCode()).body(refreshResponse);
    }

    /**
     * Logout Endpoint
     * Clear tất cả cookies và invalidate tokens
     * 
     * Note: Vì JWT là stateless nên không thể "revoke" token
     * Chỉ có thể clear cookies ở client side
     * Nếu cần revoke token thực sự, phải implement blacklist với Redis
     */
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response) {
        try {
            // Clear JWT_TOKEN cookie (legacy)
            Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);

            // Clear ACCESS_TOKEN cookie
            Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(0);
            response.addCookie(accessTokenCookie);

            // Clear REFRESH_TOKEN cookie
            Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

            Response responseLogout = new Response(200, "Logged out successfully");
            return ResponseEntity.status(responseLogout.getStatusCode()).body(responseLogout);
        } catch (Exception e) {
            Response errorResponse = new Response(500, "Logout failed: " + e.getMessage());
            return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "Auth Service is running");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}