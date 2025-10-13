package com.example.userservice.dto;

import java.io.Serializable;

public class PasswordResetResultDto implements Serializable {
    private boolean success;
    private String message;
    private UserDto user;

    public PasswordResetResultDto() {
    }

    public PasswordResetResultDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public PasswordResetResultDto(boolean success, String message, UserDto user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}