package com.academy.academymanager.security;

/**
 * Custom exception for JWT-related errors.
 * Wraps JJWT library exceptions for centralized error handling.
 */
public class JwtException extends RuntimeException {
    public JwtException(final String message) {
        super(message);
    }
    public JwtException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

