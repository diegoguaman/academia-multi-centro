package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating a Matricula.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaRequestDto {
    private String codigo;
    @NotNull(message = "Convocatoria ID is required")
    private Long idConvocatoria;
    @NotNull(message = "Student ID is required")
    private Long idAlumno;
    private Long idEntidadSubvencionadora;
    private BigDecimal importeSubvencionado;
}

