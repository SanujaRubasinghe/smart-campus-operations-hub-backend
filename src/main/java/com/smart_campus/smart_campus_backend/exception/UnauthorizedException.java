package com.smart_campus.smart_campus_backend.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String action, String resource) {
        super(String.format("You don't have permission to %s this %s", action, resource));
    }
}
