package com.example.shared.messaging;

import java.io.Serializable;

/**
 * Base class for DTOs that need correlation ID support
 * All DTOs that need to work with RabbitRpcClient should extend this class
 */
public abstract class BaseMessage implements CorrelationIdAware, Serializable {
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