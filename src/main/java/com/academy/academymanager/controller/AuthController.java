package com.academy.academymanager.controller;

import com.academy.academymanager.dto.request.LoginRequestDto;
import com.academy.academymanager.dto.request.RegisterRequestDto;
import com.academy.academymanager.dto.response.LoginResponseDto;
import com.academy.academymanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication endpoints.
 * Public endpoints (no authentication required).
 * 
 * Endpoints:
 * - POST /api/auth/login: authenticate user and return JWT
 * - POST /api/auth/register: create new user and return JWT
 * 
 * Technical Details:
 * - Uses @Valid for automatic DTO validation
 * - Returns standard HTTP status codes (200 OK, 201 CREATED)
 * - Exception handling delegated to GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    /**
     * Login endpoint.
     * Authenticates user credentials and returns JWT token.
     * 
     * Request body validation:
     * - Email: not blank, valid email format
     * - Password: not blank
     * 
     * Response:
     * - 200 OK: successful authentication with JWT token
     * - 401 UNAUTHORIZED: invalid credentials (handled by exception handler)
     * - 400 BAD REQUEST: validation errors
     * 
     * @param request login credentials
     * @return JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody final LoginRequestDto request) {
        final LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    /**
     * Registration endpoint.
     * Creates new user account and returns JWT token.
     * 
     * Request body validation:
     * - Email: not blank, valid format, unique
     * - Password: min 8 characters
     * - Nombre/Apellidos: between 2-100 characters
     * - Rol: not null
     * 
     * Response:
     * - 201 CREATED: user successfully registered with JWT token
     * - 400 BAD REQUEST: validation errors or email already exists
     * 
     * @param request registration data
     * @return JWT token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody final RegisterRequestDto request) {
        final LoginResponseDto response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

