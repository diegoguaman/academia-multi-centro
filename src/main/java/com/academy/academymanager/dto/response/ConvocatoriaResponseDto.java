package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Convocatoria response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaResponseDto {
    private Long idConvocatoria;
    private String codigo;
    private Long idCurso;
    private String nombreCurso;
    private Long idProfesor;
    private Long idCentro;
    private String nombreCentro;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

