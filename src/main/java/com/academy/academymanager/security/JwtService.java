package com.academy.academymanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT operations: generation, validation, and extraction.
 * Uses JJWT library with HMAC-SHA algorithms for signing tokens.
 * 
 * Technical Details:
 * - Tokens are stateless (no server-side session storage required)
 * - Claims include: subject (username), roles, issued-at, expiration
 * - Secret key must be at least 256 bits for HS256 algorithm
 */
@Service
public class JwtService {
    @Value("${jwt.secret.key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;
    @Value("${jwt.expiration.time:86400000}")
    private Long jwtExpirationTime;
    /**
     * Extracts username from JWT token.
     * @param token JWT token string
     * @return username (subject) from token
     */
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * Generic method to extract any claim from token.
     * Uses Function interface for flexible claim extraction.
     * 
     * @param token JWT token
     * @param claimsResolver function to extract specific claim
     * @param <T> type of claim to extract
     * @return extracted claim value
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Generates JWT token with user details and role.
     * Token structure: Header.Payload.Signature
     * 
     * @param userDetails Spring Security UserDetails object
     * @return signed JWT token string
     */
    public String generateToken(final UserDetails userDetails) {
        final Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities());
        return generateToken(extraClaims, userDetails);
    }
    /**
     * Generates token with custom claims.
     * Implementation details:
     * - setSubject: stores username
     * - setIssuedAt: current timestamp for audit
     * - setExpiration: current time + expiration time (default 24h)
     * - signWith: uses HMAC-SHA algorithm with secret key
     * 
     * @param extraClaims additional claims (e.g., roles)
     * @param userDetails user information
     * @return JWT token
     */
    public String generateToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSignInKey())
                .compact();
    }
    /**
     * Validates token against user details.
     * Checks:
     * 1. Username matches (null-safe comparison)
     * 2. Token not expired
     * 
     * @param token JWT token
     * @param userDetails user details to validate against
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        // Null-safe check: if username is null, token is invalid
        if (username == null) {
            return false;
        }
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    /**
     * Checks if token is expired.
     * @param token JWT token
     * @return true if expired
     */
    private boolean isTokenExpired(final String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            // Token inválido (malformed, invalid signature, etc.) = considerado no válido
            // Retornar true hace que isTokenValid() retorne false
            return true;
        }
    }
    /**
     * Extracts expiration date from token.
     * @param token JWT token
     * @return expiration date
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    /**
     * Parses and extracts all claims from token.
     * Try-catch handles JWT exceptions:
     * - ExpiredJwtException: token expired
     * - MalformedJwtException: invalid token structure
     * - SignatureException: signature verification failed
     * 
     * @param token JWT token
     * @return claims object
     * @throws JwtException if token invalid
     */
    private Claims extractAllClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token has expired", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid token format", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid token signature", e);
        } catch (Exception e) {
            throw new JwtException("Token validation failed", e);
        }
    }
    /**
     * Generates signing key from secret.
     * Uses HMAC-SHA algorithms (requires key >= 256 bits).
     * 
     * @return SecretKey for signing/verifying tokens
     */
    private SecretKey getSignInKey() {
        final byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

