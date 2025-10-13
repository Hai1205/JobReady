package com.example.authservice.messaging;

/**
 * Custom exception for RabbitMQ RPC errors
 */
public class RabbitRpcException extends RuntimeException {

    private final ErrorType errorType;

    public enum ErrorType {
        TIMEOUT,
        CONNECTION_ERROR,
        SERIALIZATION_ERROR,
        RESPONSE_ERROR,
        UNKNOWN
    }

    public RabbitRpcException(String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN;
    }

    public RabbitRpcException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = ErrorType.UNKNOWN;
    }

    public RabbitRpcException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public RabbitRpcException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Check if this is a timeout exception
     * 
     * @return true if this is a timeout exception
     */
    public boolean isTimeout() {
        return errorType == ErrorType.TIMEOUT;
    }

    /**
     * Check if this is a connection error
     * 
     * @return true if this is a connection error
     */
    public boolean isConnectionError() {
        return errorType == ErrorType.CONNECTION_ERROR;
    }

    /**
     * Check if this is a serialization error
     * 
     * @return true if this is a serialization error
     */
    public boolean isSerializationError() {
        return errorType == ErrorType.SERIALIZATION_ERROR;
    }

    /**
     * Check if this is a response error
     * 
     * @return true if this is a response error
     */
    public boolean isResponseError() {
        return errorType == ErrorType.RESPONSE_ERROR;
    }

    public static RabbitRpcException timeout(String message) {
        return new RabbitRpcException(ErrorType.TIMEOUT, message);
    }

    public static RabbitRpcException connectionError(String message, Throwable cause) {
        return new RabbitRpcException(ErrorType.CONNECTION_ERROR, message, cause);
    }

    public static RabbitRpcException serializationError(String message, Throwable cause) {
        return new RabbitRpcException(ErrorType.SERIALIZATION_ERROR, message, cause);
    }

    public static RabbitRpcException responseError(String message) {
        return new RabbitRpcException(ErrorType.RESPONSE_ERROR, message);
    }
}