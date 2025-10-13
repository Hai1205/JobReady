package com.example.userservice.dto.requests;

import java.io.Serializable;
import java.util.UUID;

public class ChangePasswordRequest implements Serializable {
    private UUID userId;
    private String currentPassword;
    private String newPassword;
    private String correlationId;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(UUID userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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