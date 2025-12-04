package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.ConvocatoriaRequestDto;
import com.academy.academymanager.dto.response.ConvocatoriaResponseDto;
import com.academy.academymanager.service.ConvocatoriaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

/**
 * GraphQL resolver for Convocatoria operations.
 * 
 * Demonstrates:
 * - Query resolvers with filtering
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class ConvocatoriaResolver {
    private final ConvocatoriaService convocatoriaService;
    /**
     * Query to get a single convocatoria by ID.
     * 
     * Example GraphQL query:
     * {
     *   convocatoria(id: "1") {
     *     codigo
     *     fechaInicio
     *     fechaFin
     *     curso { nombre }
     *     profesor { email }
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto convocatoria(@Argument final Long id) {
        return convocatoriaService.findById(id);
    }
    /**
     * Query to get all convocatorias with optional filters.
     * 
     * Example GraphQL query:
     * {
     *   convocatorias(activo: true, idCentro: 1) {
     *     codigo
     *     fechaInicio
     *     fechaFin
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<ConvocatoriaResponseDto> convocatorias(
            @Argument final Boolean activo,
            @Argument final Long idCentro
    ) {
        if (idCentro != null) {
            return convocatoriaService.findByCentroId(idCentro);
        }
        if (activo != null && activo) {
            return convocatoriaService.findActive();
        }
        return convocatoriaService.findAll();
    }
    /**
     * Mutation to create a new convocatoria.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createConvocatoria(input: {
     *     idCurso: 1
     *     idProfesor: 2
     *     idCentro: 3
     *     fechaInicio: "2024-01-15"
     *     fechaFin: "2024-06-15"
     *   }) {
     *     idConvocatoria
     *     codigo
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto createConvocatoria(@Argument final ConvocatoriaInput input) {
        ConvocatoriaRequestDto requestDto = ConvocatoriaRequestDto.builder()
                .codigo(input.getCodigo())
                .idCurso(input.getIdCurso())
                .idProfesor(input.getIdProfesor())
                .idCentro(input.getIdCentro())
                .fechaInicio(input.getFechaInicio())
                .fechaFin(input.getFechaFin())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return convocatoriaService.create(requestDto);
    }
    /**
     * Mutation to update an existing convocatoria.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto updateConvocatoria(
            @Argument final Long id,
            @Argument final ConvocatoriaInput input
    ) {
        ConvocatoriaRequestDto requestDto = ConvocatoriaRequestDto.builder()
                .codigo(input.getCodigo())
                .idCurso(input.getIdCurso())
                .idProfesor(input.getIdProfesor())
                .idCentro(input.getIdCentro())
                .fechaInicio(input.getFechaInicio())
                .fechaFin(input.getFechaFin())
                .activo(input.getActivo())
                .build();
        return convocatoriaService.update(id, requestDto);
    }
    /**
     * Mutation to delete a convocatoria.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteConvocatoria(@Argument final Long id) {
        convocatoriaService.delete(id);
        return true;
    }
    /**
     * Input type for Convocatoria mutations.
     * Maps to the ConvocatoriaInput type defined in schema.graphqls.
     */
    @Data
    public static class ConvocatoriaInput {
        private String codigo;
        private Long idCurso;
        private Long idProfesor;
        private Long idCentro;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo;
    }
}

