package com.example.userservice.dto.requests;

import java.io.Serializable;

public class ForgotPasswordRequest implements Serializable {
    private String email;
    private String newPassword;
    private String correlationId;

    public ForgotPasswordRequest() {
    }

    public ForgotPasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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