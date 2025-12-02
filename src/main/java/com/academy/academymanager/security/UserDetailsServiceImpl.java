package com.academy.academymanager.security;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Spring Security's UserDetailsService.
 * Loads user-specific data from database for authentication.
 * 
 * Technical Details:
 * - Integrates JPA repository with Spring Security
 * - Converts domain Usuario entity to Security UserDetails
 * - Handles role-based authorization (ROLE_ prefix required)
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    /**
     * Loads user by email (username in our context).
     * 
     * Flow:
     * 1. Query database via repository
     * 2. Check if user exists and is active
     * 3. Convert Usuario.Rol enum to GrantedAuthority
     * 4. Build Spring Security UserDetails object
     * 
     * @param username user email
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user not found or inactive
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + username
                ));
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
    /**
     * Converts Usuario roles to Spring Security authorities.
     * 
     * Technical note:
     * - Spring Security requires "ROLE_" prefix for role-based security
     * - SimpleGrantedAuthority implements GrantedAuthority interface
     * - Returns list for future multi-role support (currently single role per user)
     * 
     * @param usuario domain entity
     * @return list of granted authorities
     */
    private List<GrantedAuthority> getAuthorities(final Usuario usuario) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final String roleName = "ROLE_" + usuario.getRol().name();
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }
}

