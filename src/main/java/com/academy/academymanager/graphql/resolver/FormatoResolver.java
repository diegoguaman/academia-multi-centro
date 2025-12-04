package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.FormatoRequestDto;
import com.academy.academymanager.dto.response.FormatoResponseDto;
import com.academy.academymanager.service.FormatoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Formato operations.
 * 
 * Demonstrates:
 * - Query resolvers
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class FormatoResolver {
    private final FormatoService formatoService;
    /**
     * Query to get a single formato by ID.
     * 
     * Example GraphQL query:
     * {
     *   formato(id: "1") {
     *     nombre
     *     descripcion
     *     activo
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public FormatoResponseDto formato(@Argument final Long id) {
        return formatoService.findById(id);
    }
    /**
     * Query to get all formatos.
     * 
     * Example GraphQL query:
     * {
     *   formatos {
     *     nombre
     *     descripcion
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<FormatoResponseDto> formatos() {
        return formatoService.findAll();
    }
    /**
     * Mutation to create a new formato.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createFormato(input: {
     *     nombre: "Presencial"
     *   }) {
     *     idFormato
     *     nombre
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public FormatoResponseDto createFormato(@Argument final FormatoInput input) {
        FormatoRequestDto requestDto = FormatoRequestDto.builder()
                .nombre(input.getNombre())
                .build();
        return formatoService.create(requestDto);
    }
    /**
     * Mutation to update an existing formato.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public FormatoResponseDto updateFormato(
            @Argument final Long id,
            @Argument final FormatoInput input
    ) {
        FormatoRequestDto requestDto = FormatoRequestDto.builder()
                .nombre(input.getNombre())
                .build();
        return formatoService.update(id, requestDto);
    }
    /**
     * Mutation to delete a formato.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteFormato(@Argument final Long id) {
        formatoService.delete(id);
        return true;
    }
    /**
     * Input type for Formato mutations.
     * Maps to the FormatoInput type defined in schema.graphqls.
     */
    @Data
    public static class FormatoInput {
        private String nombre;
    }
}

