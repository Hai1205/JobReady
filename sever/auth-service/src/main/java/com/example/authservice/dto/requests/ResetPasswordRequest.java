package com.example.authservice.dto.requests;

import com.example.authservice.messaging.CorrelationIdAware;
import java.io.Serializable;

public class ResetPasswordRequest implements CorrelationIdAware, Serializable {
    private String userId;
    private String newPassword;
    private String correlationId;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String userId) {
        this.userId = userId;
    }

    public ResetPasswordRequest(String userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}