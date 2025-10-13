package com.example.userservice.messaging;

/**
 * Base class for messages that need correlation ID support
 */
public abstract class BaseMessage implements CorrelationIdAware {
    private String correlationId;

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}