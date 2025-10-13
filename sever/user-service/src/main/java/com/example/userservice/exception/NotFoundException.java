package com.example.userservice.exception;

public class NotFoundException extends OurException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}