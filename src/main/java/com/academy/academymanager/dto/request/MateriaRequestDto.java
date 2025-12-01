package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating a Materia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaRequestDto {
    @NotBlank(message = "Name is required")
    private String nombre;
    private Boolean activo;
}

