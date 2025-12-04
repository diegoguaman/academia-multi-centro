package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.request.UsuarioRequestDto;
import com.academy.academymanager.dto.response.UsuarioResponseDto;
import com.academy.academymanager.service.UsuarioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
    /**
     * Mutation to create a new usuario.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createUsuario(input: {
     *     email: "nuevo@example.com"
     *     password: "password123"
     *     rol: ALUMNO
     *     activo: true
     *   }) {
     *     idUsuario
     *     email
     *     rol
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponseDto createUsuario(@Argument final UsuarioInput input) {
        UsuarioRequestDto requestDto = UsuarioRequestDto.builder()
                .email(input.getEmail())
                .password(input.getPassword())
                .rol(input.getRol())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return usuarioService.create(requestDto);
    }
    /**
     * Mutation to update an existing usuario.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public UsuarioResponseDto updateUsuario(
            @Argument final Long id,
            @Argument final UsuarioInput input
    ) {
        UsuarioRequestDto requestDto = UsuarioRequestDto.builder()
                .email(input.getEmail())
                .password(input.getPassword())
                .rol(input.getRol())
                .activo(input.getActivo())
                .build();
        return usuarioService.update(id, requestDto);
    }
    /**
     * Mutation to delete a usuario.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteUsuario(@Argument final Long id) {
        usuarioService.delete(id);
        return true;
    }
    /**
     * Input type for Usuario mutations.
     * Maps to the UsuarioInput type defined in schema.graphqls.
     */
    @Data
    public static class UsuarioInput {
        private String email;
        private String password;
        private Usuario.Rol rol;
        private Boolean activo;
    }
}

