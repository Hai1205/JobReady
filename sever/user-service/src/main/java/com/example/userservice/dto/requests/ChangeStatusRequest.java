package com.example.userservice.dto.requests;

import java.io.Serializable;
import java.util.UUID;

public class ChangeStatusRequest implements Serializable {
    private UUID userId;
    private String status;
    private String correlationId;

    public ChangeStatusRequest() {
    }

    public ChangeStatusRequest(UUID userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
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