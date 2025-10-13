package com.example.authservice.exception;

public class UnauthorizedException extends OurException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}