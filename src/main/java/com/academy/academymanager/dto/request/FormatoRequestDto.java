package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating a Formato.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormatoRequestDto {
    @NotBlank(message = "Name is required")
    private String nombre;
}

