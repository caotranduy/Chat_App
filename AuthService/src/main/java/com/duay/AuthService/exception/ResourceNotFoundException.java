package com.duay.AuthService.exception;
 /**
  * Customized exception-caught response
  */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}