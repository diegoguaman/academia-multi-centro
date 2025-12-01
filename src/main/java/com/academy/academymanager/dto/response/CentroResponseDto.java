package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Centro response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CentroResponseDto {
    private Long idCentro;
    private String codigoCentro;
    private String nombre;
    private Long idEmpresa;
    private String nombreEmpresa;
    private Long idComunidad;
    private String nombreComunidad;
    private Integer capacidadMaxima;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

