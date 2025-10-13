package com.example.cvservice.messaging;

/**
 * Interface for objects that support correlation ID tracking
 */
public interface CorrelationIdAware {
    String getCorrelationId();

    void setCorrelationId(String correlationId);
}