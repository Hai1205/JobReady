package com.example.userservice.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.UUID;

/**
 * Base class for all RPC request messages.
 * Contains common metadata for tracking requests.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcRequest<T> implements Serializable {
    
    private String correlationId;
    private String command;
    private T payload;
    
    public RpcRequest() {
        // Default constructor required for Jackson
        this.correlationId = UUID.randomUUID().toString();
    }
    
    public RpcRequest(String command, T payload) {
        this.correlationId = UUID.randomUUID().toString();
        this.command = command;
        this.payload = payload;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public T getPayload() {
        return payload;
    }
    
    public void setPayload(T payload) {
        this.payload = payload;
    }
}