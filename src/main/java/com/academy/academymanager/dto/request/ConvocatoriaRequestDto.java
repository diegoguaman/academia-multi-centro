package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for creating/updating a Convocatoria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaRequestDto {
    private String codigo;
    @NotNull(message = "Course ID is required")
    private Long idCurso;
    @NotNull(message = "Professor ID is required")
    private Long idProfesor;
    @NotNull(message = "Center ID is required")
    private Long idCentro;
    @NotNull(message = "Start date is required")
    private LocalDate fechaInicio;
    @NotNull(message = "End date is required")
    private LocalDate fechaFin;
    private Boolean activo;
}

