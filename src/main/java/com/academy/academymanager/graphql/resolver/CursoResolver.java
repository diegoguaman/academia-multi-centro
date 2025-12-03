package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.CursoRequestDto;
import com.academy.academymanager.dto.response.CursoResponseDto;
import com.academy.academymanager.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Curso operations.
 * 
 * Demonstrates:
 * - Query resolvers with filtering
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class CursoResolver {
    private final CursoService cursoService;
    /**
     * Query to get a single curso by ID.
     * 
     * Example GraphQL query:
     * {
     *   curso(id: "1") {
     *     nombre
     *     precioBase
     *     materia { nombre }
     *     formato { nombre }
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public CursoResponseDto curso(@Argument final Long id) {
        return cursoService.findById(id);
    }
    /**
     * Query to get all cursos with optional filter.
     * 
     * Example GraphQL query:
     * {
     *   cursos(activo: true) {
     *     nombre
     *     precioBase
     *     duracionHoras
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<CursoResponseDto> cursos(@Argument final Boolean activo) {
        if (activo != null && activo) {
            return cursoService.findActive();
        }
        return cursoService.findAll();
    }
    /**
     * Mutation to create a new curso.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createCurso(input: {
     *     nombre: "Java Avanzado"
     *     idMateria: 1
     *     idFormato: 2
     *     precioBase: 500.00
     *     duracionHoras: 40
     *   }) {
     *     idCurso
     *     nombre
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public CursoResponseDto createCurso(@Argument final CursoInput input) {
        CursoRequestDto requestDto = CursoRequestDto.builder()
                .nombre(input.getNombre())
                .idMateria(input.getIdMateria())
                .idFormato(input.getIdFormato())
                .precioBase(input.getPrecioBase())
                .duracionHoras(input.getDuracionHoras())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return cursoService.create(requestDto);
    }
    /**
     * Mutation to update an existing curso.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public CursoResponseDto updateCurso(
            @Argument final Long id,
            @Argument final CursoInput input
    ) {
        CursoRequestDto requestDto = CursoRequestDto.builder()
                .nombre(input.getNombre())
                .idMateria(input.getIdMateria())
                .idFormato(input.getIdFormato())
                .precioBase(input.getPrecioBase())
                .duracionHoras(input.getDuracionHoras())
                .activo(input.getActivo())
                .build();
        return cursoService.update(id, requestDto);
    }
    /**
     * Mutation to delete a curso.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteCurso(@Argument final Long id) {
        cursoService.delete(id);
        return true;
    }
    /**
     * Input type for Curso mutations.
     */
    public static class CursoInput {
        private String nombre;
        private Long idMateria;
        private Long idFormato;
        private java.math.BigDecimal precioBase;
        private Integer duracionHoras;
        private Boolean activo;
        public String getNombre() {
            return nombre;
        }
        public void setNombre(final String nombre) {
            this.nombre = nombre;
        }
        public Long getIdMateria() {
            return idMateria;
        }
        public void setIdMateria(final Long idMateria) {
            this.idMateria = idMateria;
        }
        public Long getIdFormato() {
            return idFormato;
        }
        public void setIdFormato(final Long idFormato) {
            this.idFormato = idFormato;
        }
        public java.math.BigDecimal getPrecioBase() {
            return precioBase;
        }
        public void setPrecioBase(final java.math.BigDecimal precioBase) {
            this.precioBase = precioBase;
        }
        public Integer getDuracionHoras() {
            return duracionHoras;
        }
        public void setDuracionHoras(final Integer duracionHoras) {
            this.duracionHoras = duracionHoras;
        }
        public Boolean getActivo() {
            return activo;
        }
        public void setActivo(final Boolean activo) {
            this.activo = activo;
        }
    }
}

