package com.example.authservice.dto.responses;

import java.io.Serializable;

public class MessageResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String message;
    private boolean success;

    public MessageResponse() {
    }

    public MessageResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}