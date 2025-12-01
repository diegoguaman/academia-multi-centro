package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating DatosPersonales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPersonalesRequestDto {
    @NotNull(message = "User ID is required")
    private Long idUsuario;
    @NotBlank(message = "Name is required")
    private String nombre;
    @NotBlank(message = "Surname is required")
    private String apellidos;
    private String dniNie;
    private String telefono;
    private String direccion;
    private BigDecimal discapacidadPorcentaje;
    private Boolean esFamiliaNumerosa;
}

