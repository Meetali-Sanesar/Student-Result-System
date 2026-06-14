package com.srms.exception;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message, 400);
    }
}
