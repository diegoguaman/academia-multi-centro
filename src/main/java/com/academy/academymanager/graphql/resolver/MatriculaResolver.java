package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.domain.entity.Matricula;
import com.academy.academymanager.dto.request.MatriculaRequestDto;
import com.academy.academymanager.dto.response.MatriculaResponseDto;
import com.academy.academymanager.mapper.MatriculaMapper;
import com.academy.academymanager.service.MatriculaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * GraphQL resolver for Matricula operations.
 * 
 * Demonstrates:
 * - Query resolvers (@QueryMapping)
 * - Mutation resolvers (@MutationMapping)
 * - Batch mapping for N+1 problem prevention (@BatchMapping)
 * - Field-level authorization (@PreAuthorize)
 */
@Controller
@RequiredArgsConstructor
public class MatriculaResolver {
    private final MatriculaService matriculaService;
    /**
     * Query to get a single matrícula by ID.
     * 
     * Example GraphQL query:
     * {
     *   matricula(id: "1") {
     *     codigo
     *     precioFinal
     *     alumno { email }
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public MatriculaResponseDto matricula(@Argument final Long id) {
        return matriculaService.findById(id);
    }
    /**
     * Query to get all matrículas with optional filters.
     * 
     * Example GraphQL query:
     * {
     *   matriculas(estadoPago: PAGADO) {
     *     codigo
     *     precioFinal
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public List<MatriculaResponseDto> matriculas(
            @Argument final Matricula.EstadoPago estadoPago,
            @Argument final Long idAlumno
    ) {
        if (idAlumno != null) {
            return matriculaService.findByAlumno(idAlumno);
        }
        List<MatriculaResponseDto> all = matriculaService.findAll();
        if (estadoPago != null) {
            return all.stream()
                    .filter(m -> m.getEstadoPago() == estadoPago)
                    .toList();
        }
        return all;
    }
    /**
     * Mutation to create a new matrícula.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createMatricula(input: {
     *     idConvocatoria: 1
     *     idAlumno: 5
     *   }) {
     *     idMatricula
     *     codigo
     *     precioFinal
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public MatriculaResponseDto createMatricula(@Argument final MatriculaInput input) {
        MatriculaRequestDto requestDto = MatriculaRequestDto.builder()
                .idConvocatoria(input.getIdConvocatoria())
                .idAlumno(input.getIdAlumno())
                .idEntidadSubvencionadora(input.getIdEntidadSubvencionadora())
                .importeSubvencionado(input.getImporteSubvencionado())
                .build();
        return matriculaService.create(requestDto);
    }
    /**
     * Mutation to update an existing matrícula.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public MatriculaResponseDto updateMatricula(
            @Argument final Long id,
            @Argument final MatriculaInput input
    ) {
        MatriculaRequestDto requestDto = MatriculaRequestDto.builder()
                .idConvocatoria(input.getIdConvocatoria())
                .idAlumno(input.getIdAlumno())
                .idEntidadSubvencionadora(input.getIdEntidadSubvencionadora())
                .importeSubvencionado(input.getImporteSubvencionado())
                .build();
        return matriculaService.update(id, requestDto);
    }
    /**
     * Mutation to delete a matrícula.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteMatricula(@Argument final Long id) {
        matriculaService.delete(id);
        return true;
    }
    /**
     * Batch mapping to resolve calificaciones for multiple matrículas.
     * This prevents N+1 problem: instead of 1 query + N queries (one per matrícula),
     * it does 1 query + 1 batch query for all calificaciones.
     * 
     * GraphQL will automatically call this when calificaciones field is requested:
     * {
     *   matriculas {
     *     codigo
     *     calificaciones {  <- This triggers batch mapping
     *       nota
     *     }
     *   }
     * }
     * 
     * TODO: Implementar cuando se cree CalificacionResponseDto
     */
    // @BatchMapping
    // public Map<MatriculaResponseDto, List<Object>> calificaciones(
    //         final List<MatriculaResponseDto> matriculas
    // ) {
    //     return java.util.Collections.emptyMap();
    // }
    /**
     * Input type for Matricula mutations.
     * Maps to the MatriculaInput type defined in schema.graphqls.
     */
    @Data
    public static class MatriculaInput {
        private Long idConvocatoria;
        private Long idAlumno;
        private Long idEntidadSubvencionadora;
        private BigDecimal importeSubvencionado;
    }
}

