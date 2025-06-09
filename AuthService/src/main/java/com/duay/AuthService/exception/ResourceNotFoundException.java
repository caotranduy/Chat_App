package com.duay.AuthService.exception;

// REMOVE the @ResponseStatus annotation from here
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}