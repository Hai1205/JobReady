package com.example.authservice.dto.requests;

public class TokenValidationRequest {
    private String token;
    private String username;

    public TokenValidationRequest() {
    }

    public TokenValidationRequest(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}