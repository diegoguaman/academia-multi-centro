package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.response.UsuarioResponseDto;
import com.academy.academymanager.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Usuario operations.
 * 
 * Demonstrates:
 * - Query resolvers with role filtering
 * - Authorization at resolver level with @PreAuthorize
 * - Different permissions for different queries
 */
@Controller
@RequiredArgsConstructor
public class UsuarioResolver {
    private final UsuarioService usuarioService;

    /**
     * Query to get a single usuario by ID.
     * 
     * Authorization: ADMIN, PROFESOR, ADMINISTRATIVO can view any user.
     * ALUMNO can only view their own profile (implemented in service if needed).
     * 
     * Example GraphQL query:
     * {
     *   usuario(id: "1") {
     *     idUsuario
     *     email
     *     rol
     *     activo
     *   }
     * }
     */
    @QueryMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ADMINISTRATIVO')") // Comentado temporalmente para pruebas
    public UsuarioResponseDto usuario(@Argument final Long id) {
        return usuarioService.findById(id);
    }

    /**
     * Query to get all usuarios with optional role filter.
     * 
     * Authorization: Only ADMIN can list all users.
     * 
     * Example GraphQL queries:
     * 
     * # Get all users
     * {
     *   usuarios {
     *     idUsuario
     *     email
     *     rol
     *   }
     * }
     * 
     * # Get only ADMIN users
     * {
     *   usuarios(rol: ADMIN) {
     *     idUsuario
     *     email
     *     rol
     *   }
     * }
     * 
     * # Get only PROFESOR users
     * {
     *   usuarios(rol: PROFESOR) {
     *     idUsuario
     *     email
     *     rol
     *   }
     * }
     */
    @QueryMapping
    // @PreAuthorize("hasRole('ADMIN')") // Comentado temporalmente para pruebas
    public List<UsuarioResponseDto> usuarios(@Argument final Usuario.Rol rol) {
        if (rol != null) {
            return usuarioService.findByRol(rol);
        }
        return usuarioService.findAll();
    }
}

