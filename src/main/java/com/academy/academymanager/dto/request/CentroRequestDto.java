package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating a Centro.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CentroRequestDto {
    @NotBlank(message = "Center code is required")
    private String codigoCentro;
    @NotBlank(message = "Name is required")
    private String nombre;
    @NotNull(message = "Company ID is required")
    private Long idEmpresa;
    @NotNull(message = "Community ID is required")
    private Long idComunidad;
    @PositiveOrZero(message = "Capacity must be positive or zero")
    private Integer capacidadMaxima;
    private Boolean activo;
}

