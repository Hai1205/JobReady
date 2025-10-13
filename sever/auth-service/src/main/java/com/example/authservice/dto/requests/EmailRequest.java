package com.example.authservice.dto.requests;

import com.example.authservice.messaging.CorrelationIdAware;
import java.io.Serializable;

public class EmailRequest implements CorrelationIdAware, Serializable {
    private String email;
    private String correlationId;

    public EmailRequest() {
    }

    public EmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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