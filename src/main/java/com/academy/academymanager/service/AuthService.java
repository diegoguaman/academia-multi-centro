package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.DatosPersonales;
import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.request.LoginRequestDto;
import com.academy.academymanager.dto.request.RegisterRequestDto;
import com.academy.academymanager.dto.response.LoginResponseDto;
import com.academy.academymanager.repository.DatosPersonalesRepository;
import com.academy.academymanager.repository.UsuarioRepository;
import com.academy.academymanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations: login and registration.
 * Integrates Spring Security AuthenticationManager with JWT generation.
 * 
 * Business Logic:
 * - Login: validates credentials, generates JWT token
 * - Register: creates new user with hashed password, generates token
 * - Validates email uniqueness before registration
 * 
 * Technical Details:
 * - Uses @Transactional for atomic database operations
 * - Password hashing with BCrypt before persisting
 * - Exception handling for authentication failures
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final DatosPersonalesRepository datosPersonalesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.expiration.time:86400000}")
    private Long jwtExpirationTime;
    /**
     * Authenticates user and generates JWT token.
     * 
     * Flow:
     * 1. Validate credentials via AuthenticationManager
     * 2. Load user details from database
     * 3. Generate JWT token with user info
     * 4. Build response DTO with token and user data
     * 
     * Exception handling:
     * - BadCredentialsException: invalid email or password
     * - UsernameNotFoundException: user not found
     * 
     * @param request login credentials
     * @return login response with JWT token
     * @throws BadCredentialsException if authentication fails
     */
    @Transactional(readOnly = true)
    public LoginResponseDto login(final LoginRequestDto request) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtService.generateToken(userDetails);
            final Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            final String nombreCompleto = buildNombreCompleto(usuario);
            return LoginResponseDto.builder()
                    .token(jwtToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationTime)
                    .email(usuario.getEmail())
                    .rol(usuario.getRol())
                    .nombre(nombreCompleto)
                    .build();
        } catch (BadCredentialsException e) {
            // Re-throw BadCredentialsException from authentication manager
            throw new BadCredentialsException("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            // Handle case when user is not found or account is disabled
            // This can happen when loadUserByUsername is called after authentication
            // but user was disabled between authentication and user details loading
            throw new BadCredentialsException("Invalid email or password");
        }
    }
    /**
     * Registers new user and generates JWT token.
     * 
     * Flow:
     * 1. Validate email uniqueness
     * 2. Hash password with BCrypt
     * 3. Create Usuario entity
     * 4. Create DatosPersonales entity (one-to-one relationship)
     * 5. Persist to database
     * 6. Generate JWT token
     * 7. Build response DTO
     * 
     * Conditional validation:
     * - if (email exists): throw IllegalArgumentException
     * 
     * Transaction management:
     * - @Transactional ensures both entities saved atomically
     * - Rollback on any exception
     * 
     * @param request registration data
     * @return login response with JWT token
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    public LoginResponseDto register(final RegisterRequestDto request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        final Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .activo(true)
                .build();
        final Usuario savedUsuario = usuarioRepository.save(usuario);
        final DatosPersonales datosPersonales = DatosPersonales.builder()
                .usuario(savedUsuario)
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .build();
        datosPersonalesRepository.save(datosPersonales);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUsuario.getEmail());
        final String jwtToken = jwtService.generateToken(userDetails);
        return LoginResponseDto.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationTime)
                .email(savedUsuario.getEmail())
                .rol(savedUsuario.getRol())
                .nombre(request.getNombre() + " " + request.getApellidos())
                .build();
    }
    /**
     * Helper method to build full name from usuario entity.
     * Handles null DatosPersonales with conditional check.
     * 
     * @param usuario user entity
     * @return full name or email if personal data not available
     */
    private String buildNombreCompleto(final Usuario usuario) {
        if (usuario.getDatosPersonales() != null) {
            return usuario.getDatosPersonales().getNombre() + " " +
                    usuario.getDatosPersonales().getApellidos();
        }
        return usuario.getEmail();
    }
}

