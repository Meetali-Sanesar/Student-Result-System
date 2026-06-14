package com.srms.exception;

/**
 * Thrown when authentication fails (invalid credentials, expired session).
 */
public class AuthException extends AppException {

    public AuthException(String message) {
        super(message, 401);
    }
}
