package com.academy.academymanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts every HTTP request.
 * Extends OncePerRequestFilter to guarantee single execution per request.
 * 
 * Filter Chain Flow:
 * 1. Extract JWT from Authorization header
 * 2. Validate token format (Bearer prefix)
 * 3. Extract username from token
 * 4. Load user details from database
 * 5. Validate token signature and expiration
 * 6. Set authentication in SecurityContext
 * 
 * Technical Details:
 * - Filter executed before Spring Security's authentication filters
 * - Uses SecurityContextHolder (ThreadLocal) for storing authentication
 * - Try-catch handles JWT exceptions without breaking filter chain
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    /**
     * Main filter method executed for each request.
     * 
     * Conditional logic:
     * - if (authHeader == null || !starts with "Bearer "): skip JWT processing
     * - if (username != null && no auth in context): proceed with authentication
     * - if (token valid): set authentication in SecurityContext
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain chain of filters to execute
     * @throws ServletException if filter error
     * @throws IOException if I/O error
     */
    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip JWT processing for Actuator endpoints (used by K8s health checks)
        final String requestPath = request.getRequestURI();
        if (requestPath != null && requestPath.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwtToken = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
            final String userEmail = jwtService.extractUsername(jwtToken);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    final UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (JwtException e) {
            logger.error("JWT validation failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}

