package com.example.rabbitmq.exceptions;

/**
 * Exception thrown when an RPC remote service returns a non-200 response.
 * Carries the remote status code so callers can map it to local HTTP errors.
 */
public class RemoteRpcException extends RuntimeException {
    private final int code;

    public RemoteRpcException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
