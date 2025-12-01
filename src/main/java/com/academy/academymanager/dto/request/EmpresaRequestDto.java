package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating an Empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaRequestDto {
    @NotBlank(message = "CIF is required")
    private String cif;
    @NotBlank(message = "Legal name is required")
    private String nombreLegal;
    private String direccionFiscal;
    private Boolean activo;
}

