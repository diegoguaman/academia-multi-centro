package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for DatosPersonales response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPersonalesResponseDto {
    private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String dniNie;
    private String telefono;
    private String direccion;
    private BigDecimal discapacidadPorcentaje;
    private Boolean esFamiliaNumerosa;
    private LocalDateTime fechaModificacion;
}

