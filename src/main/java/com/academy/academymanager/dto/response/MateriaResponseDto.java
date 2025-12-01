package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Materia response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaResponseDto {
    private Long idMateria;
    private String nombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

