package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating a Calificacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionRequestDto {
    @NotNull(message = "Matricula ID is required")
    private Long idMatricula;
    @DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Grade must be at most 10.0")
    private BigDecimal nota;
    private String comentarios;
}

