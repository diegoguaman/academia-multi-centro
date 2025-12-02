package com.academy.academymanager.exception;

import com.academy.academymanager.security.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 * Provides centralized error handling with consistent response format.
 * 
 * Technical Details:
 * - @RestControllerAdvice applies to all controllers
 * - Catches specific exceptions and maps to HTTP status codes
 * - Returns standardized error response with timestamp and details
 * 
 * Exception Handling Strategy:
 * - Validation errors: 400 BAD REQUEST
 * - Authentication errors: 401 UNAUTHORIZED
 * - Authorization errors: 403 FORBIDDEN
 * - Not found errors: 404 NOT FOUND
 * - Business logic errors: 422 UNPROCESSABLE ENTITY
 * - Server errors: 500 INTERNAL SERVER ERROR
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles validation errors from @Valid annotation.
     * 
     * Flow:
     * 1. Extract field errors from exception
     * 2. Build map of field -> error message
     * 3. Return 400 BAD REQUEST with details
     * 
     * Loop logic:
     * - for (FieldError error : bindingResult.getFieldErrors())
     * - Accumulates all validation errors in map
     * 
     * @param exception validation exception
     * @return error response with field-specific messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            final MethodArgumentNotValidException exception
    ) {
        final Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors
        );
    }
    /**
     * Handles authentication failures (invalid credentials).
     * 
     * @param exception bad credentials exception
     * @return 401 UNAUTHORIZED response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            final BadCredentialsException exception
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }
    /**
     * Handles user not found errors.
     * 
     * @param exception username not found exception
     * @return 401 UNAUTHORIZED response
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(
            final UsernameNotFoundException exception
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }
    /**
     * Handles JWT-related errors (invalid token, expired, malformed).
     * 
     * @param exception JWT exception
     * @return 401 UNAUTHORIZED response
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(
            final JwtException exception
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }
    /**
     * Handles business logic errors (e.g., email already exists).
     * 
     * @param exception illegal argument exception
     * @return 422 UNPROCESSABLE ENTITY response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            final IllegalArgumentException exception
    ) {
        return buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage(),
                null
        );
    }
    /**
     * Handles generic server errors.
     * Fallback handler for unexpected exceptions.
     * 
     * Try-catch equivalent: catch all unhandled exceptions
     * 
     * @param exception any exception
     * @return 500 INTERNAL SERVER ERROR response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            final Exception exception
    ) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + exception.getMessage(),
                null
        );
    }
    /**
     * Helper method to build standardized error response.
     * 
     * Response format:
     * {
     *   "timestamp": "2024-12-02T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Validation failed",
     *   "details": { "field": "error message" }
     * }
     * 
     * @param status HTTP status code
     * @param message error message
     * @param details additional error details (optional)
     * @return formatted error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            final HttpStatus status,
            final String message,
            final Object details
    ) {
        final Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        if (details != null) {
            errorResponse.put("details", details);
        }
        return ResponseEntity.status(status).body(errorResponse);
    }
}

