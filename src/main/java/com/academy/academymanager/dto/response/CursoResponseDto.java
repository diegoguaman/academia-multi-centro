package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Curso response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDto {
    private Long idCurso;
    private String nombre;
    private Long idMateria;
    private String nombreMateria;
    private Long idFormato;
    private String nombreFormato;
    private BigDecimal precioBase;
    private Integer duracionHoras;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

