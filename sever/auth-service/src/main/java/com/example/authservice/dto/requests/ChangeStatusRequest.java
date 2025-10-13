package com.example.authservice.dto.requests;

import java.io.Serializable;

public class ChangeStatusRequest implements Serializable {
    private String userId;
    private String status;
    private String correlationId;

    public ChangeStatusRequest() {
    }

    public ChangeStatusRequest(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}