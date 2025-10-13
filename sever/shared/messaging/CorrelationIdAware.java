package com.example.shared.messaging;

/**
 * Interface for messages that support correlation ID
 * This allows the RabbitRpcClient to automatically set correlationId for requests
 */
public interface CorrelationIdAware {
    String getCorrelationId();
    void setCorrelationId(String correlationId);
}