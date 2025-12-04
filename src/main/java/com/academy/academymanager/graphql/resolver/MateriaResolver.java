package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.MateriaRequestDto;
import com.academy.academymanager.dto.response.MateriaResponseDto;
import com.academy.academymanager.service.MateriaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Materia operations.
 * 
 * Demonstrates:
 * - Query resolvers with filtering
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class MateriaResolver {
    private final MateriaService materiaService;
    /**
     * Query to get a single materia by ID.
     * 
     * Example GraphQL query:
     * {
     *   materia(id: "1") {
     *     nombre
     *     descripcion
     *     activo
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public MateriaResponseDto materia(@Argument final Long id) {
        return materiaService.findById(id);
    }
    /**
     * Query to get all materias with optional filter.
     * 
     * Example GraphQL query:
     * {
     *   materias(activo: true) {
     *     nombre
     *     descripcion
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<MateriaResponseDto> materias(@Argument final Boolean activo) {
        if (activo != null && activo) {
            return materiaService.findActive();
        }
        return materiaService.findAll();
    }
    /**
     * Mutation to create a new materia.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createMateria(input: {
     *     nombre: "Matemáticas"
     *     activo: true
     *   }) {
     *     idMateria
     *     nombre
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public MateriaResponseDto createMateria(@Argument final MateriaInput input) {
        MateriaRequestDto requestDto = MateriaRequestDto.builder()
                .nombre(input.getNombre())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return materiaService.create(requestDto);
    }
    /**
     * Mutation to update an existing materia.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public MateriaResponseDto updateMateria(
            @Argument final Long id,
            @Argument final MateriaInput input
    ) {
        MateriaRequestDto requestDto = MateriaRequestDto.builder()
                .nombre(input.getNombre())
                .activo(input.getActivo())
                .build();
        return materiaService.update(id, requestDto);
    }
    /**
     * Mutation to delete a materia.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteMateria(@Argument final Long id) {
        materiaService.delete(id);
        return true;
    }
    /**
     * Input type for Materia mutations.
     * Maps to the MateriaInput type defined in schema.graphqls.
     */
    @Data
    public static class MateriaInput {
        private String nombre;
        private Boolean activo;
    }
}

