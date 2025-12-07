package com.academy.academymanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Configuration for JWT-based authentication.
 * 
 * Architecture:
 * - Stateless authentication (no HttpSession used)
 * - JWT tokens for authorization
 * - Role-based access control (RBAC)
 * - BCrypt for password hashing
 * 
 * Technical Details:
 * - SecurityFilterChain replaces deprecated WebSecurityConfigurerAdapter
 * (Spring Security 6+)
 * - Filter chain: JwtAuthenticationFilter ->
 * UsernamePasswordAuthenticationFilter
 * - CORS enabled for frontend integration
 * - CSRF disabled (stateless JWT doesn't need CSRF protection)
 * 
 * Why JWT over Session-based Auth:
 * 1. Scalability: No server-side session storage (stateless)
 * 2. Microservices: Token can be validated by any service
 * 3. Mobile-friendly: No cookies required
 * 4. Performance: No DB lookup on every request (after initial auth)
 * 
 * Trade-offs:
 * - Token revocation requires additional logic (blacklist/whitelist)
 * - Token size larger than session ID
 * - Mitigation: Short expiration times + refresh token strategy
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configures CORS (Cross-Origin Resource Sharing) for GraphQL and API endpoints.
     * 
     * This is CRITICAL for GraphiQL to work, as it needs to load resources
     * and make requests from the browser to the server.
     * 
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow localhost origins (development)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:5173"
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow all headers (important for JWT and GraphQL)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all routes
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures HTTP security with filter chain.
     * 
     * Flow explanation:
     * 1. Disable CSRF (not needed for stateless JWT)
     * 2. Configure authorization rules (permitAll vs authenticated)
     * 3. Set session policy to STATELESS
     * 4. Add JWT filter before UsernamePasswordAuthenticationFilter
     * 5. Configure authentication provider
     * 
     * Authorization Rules:
     * - /api/auth/** : public (login, register)
     * - /api/admin/** : requires ADMIN role
     * - /api/profesor/** : requires PROFESOR role
     * - Other authenticated endpoints: requires any authenticated user
     * 
     * @param http HttpSecurity builder
     * @return configured SecurityFilterChain
     * @throws Exception if configuration error
     */

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/graphql/**", "/graphiql/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()  // Permitir todos los endpoints de Actuator para health checks de K8s
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/profesor/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers("/api/alumno/**").hasAnyRole("ALUMNO", "ADMIN")
                        .requestMatchers("/api/administrativo/**").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**").hasAnyRole("ALUMNO", "PROFESOR", "ADMIN", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.POST, "/api/cursos/**").hasAnyRole("ADMIN", "ADMINISTRATIVO")
                        .requestMatchers("/api/matriculas/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures authentication provider with custom UserDetailsService and
     * password encoder.
     * 
     * DaoAuthenticationProvider:
     * - Loads users from database via UserDetailsService
     * - Verifies passwords with PasswordEncoder (BCrypt)
     * - Throws exceptions on authentication failures
     * 
     * @return configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * AuthenticationManager bean for manual authentication (e.g., login endpoint).
     * 
     * @param config authentication configuration
     * @return authentication manager
     * @throws Exception if configuration error
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt password encoder bean.
     * 
     * Technical details:
     * - BCrypt is adaptive: can increase cost factor over time
     * - Salted hashing prevents rainbow table attacks
     * - Default strength: 10 rounds (2^10 iterations)
     * - Each hash includes salt automatically
     * 
     * Why BCrypt over SHA-256:
     * - Intentionally slow (prevents brute force)
     * - Built-in salt generation
     * - Industry standard for password hashing
     * 
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
