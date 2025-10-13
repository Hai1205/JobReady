package com.example.userservice.messaging;

/**
 * Interface for objects that support correlation ID tracking
 */
public interface CorrelationIdAware {
    String getCorrelationId();

    void setCorrelationId(String correlationId);
}