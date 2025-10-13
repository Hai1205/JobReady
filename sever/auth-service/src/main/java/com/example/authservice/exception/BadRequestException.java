package com.example.authservice.exception;

public class BadRequestException extends OurException {
    public BadRequestException(String message) {
        super(message, 400);
    }
}