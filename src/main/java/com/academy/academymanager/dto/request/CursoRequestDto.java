package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating a Curso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoRequestDto {
    @NotBlank(message = "Name is required")
    private String nombre;
    @NotNull(message = "Materia ID is required")
    private Long idMateria;
    @NotNull(message = "Formato ID is required")
    private Long idFormato;
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal precioBase;
    @Positive(message = "Duration must be positive")
    private Integer duracionHoras;
    private Boolean activo;
}

