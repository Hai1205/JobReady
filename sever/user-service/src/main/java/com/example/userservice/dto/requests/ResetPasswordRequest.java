package com.example.userservice.dto.requests;

import java.io.Serializable;
import java.util.UUID;

public class ResetPasswordRequest implements Serializable {
    private UUID userId;
    private String newPassword;
    private String correlationId;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(UUID userId) {
        this.userId = userId;
    }

    public ResetPasswordRequest(UUID userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}