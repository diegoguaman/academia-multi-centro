package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Formato response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormatoResponseDto {
    private Long idFormato;
    private String nombre;
    private LocalDateTime fechaCreacion;
}

