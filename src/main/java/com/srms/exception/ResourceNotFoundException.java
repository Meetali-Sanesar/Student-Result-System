package com.srms.exception;

/**
 * Thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
