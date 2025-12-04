package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.CentroRequestDto;
import com.academy.academymanager.dto.response.CentroResponseDto;
import com.academy.academymanager.service.CentroService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Centro operations.
 * 
 * Demonstrates:
 * - Query resolvers with filtering
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class CentroResolver {
    private final CentroService centroService;
    /**
     * Query to get a single centro by ID.
     * 
     * Example GraphQL query:
     * {
     *   centro(id: "1") {
     *     nombre
     *     codigoCentro
     *     capacidadMaxima
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public CentroResponseDto centro(@Argument final Long id) {
        return centroService.findById(id);
    }
    /**
     * Query to get all centros with optional filter.
     * 
     * Example GraphQL query:
     * {
     *   centros(activo: true) {
     *     nombre
     *     codigoCentro
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<CentroResponseDto> centros(@Argument final Boolean activo) {
        if (activo != null && activo) {
            return centroService.findActive();
        }
        return centroService.findAll();
    }
    /**
     * Mutation to create a new centro.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createCentro(input: {
     *     codigoCentro: "CENT-001"
     *     nombre: "Centro Madrid"
     *     idEmpresa: 1
     *     idComunidad: 1
     *     capacidadMaxima: 100
     *   }) {
     *     idCentro
     *     nombre
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public CentroResponseDto createCentro(@Argument final CentroInput input) {
        CentroRequestDto requestDto = CentroRequestDto.builder()
                .codigoCentro(input.getCodigoCentro())
                .nombre(input.getNombre())
                .idEmpresa(input.getIdEmpresa())
                .idComunidad(input.getIdComunidad())
                .capacidadMaxima(input.getCapacidadMaxima())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return centroService.create(requestDto);
    }
    /**
     * Mutation to update an existing centro.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public CentroResponseDto updateCentro(
            @Argument final Long id,
            @Argument final CentroInput input
    ) {
        CentroRequestDto requestDto = CentroRequestDto.builder()
                .codigoCentro(input.getCodigoCentro())
                .nombre(input.getNombre())
                .idEmpresa(input.getIdEmpresa())
                .idComunidad(input.getIdComunidad())
                .capacidadMaxima(input.getCapacidadMaxima())
                .activo(input.getActivo())
                .build();
        return centroService.update(id, requestDto);
    }
    /**
     * Mutation to delete a centro.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteCentro(@Argument final Long id) {
        centroService.delete(id);
        return true;
    }
    /**
     * Input type for Centro mutations.
     * Maps to the CentroInput type defined in schema.graphqls.
     */
    @Data
    public static class CentroInput {
        private String codigoCentro;
        private String nombre;
        private Long idEmpresa;
        private Long idComunidad;
        private Integer capacidadMaxima;
        private Boolean activo;
    }
}

