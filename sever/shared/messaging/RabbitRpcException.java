package com.example.shared.messaging;

/**
 * Exception thrown for RabbitMQ RPC communication errors
 */
public class RabbitRpcException extends RuntimeException {

    public enum ErrorType {
        TIMEOUT,
        CONNECTION_ERROR,
        SERIALIZATION_ERROR,
        INVALID_RESPONSE,
        UNKNOWN
    }

    private final ErrorType errorType;

    public RabbitRpcException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public RabbitRpcException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
    
    public boolean isTimeout() {
        return errorType == ErrorType.TIMEOUT;
    }
    
    public boolean isConnectionError() {
        return errorType == ErrorType.CONNECTION_ERROR;
    }
}