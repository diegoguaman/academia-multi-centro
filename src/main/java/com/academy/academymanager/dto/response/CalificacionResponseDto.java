package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Calificacion response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionResponseDto {
    private Long idCalificacion;
    private Long idMatricula;
    private BigDecimal nota;
    private String comentarios;
    private LocalDateTime fechaCreacion;
}

