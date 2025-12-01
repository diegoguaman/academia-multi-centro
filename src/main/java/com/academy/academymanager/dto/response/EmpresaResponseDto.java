package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Empresa response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaResponseDto {
    private Long idEmpresa;
    private String cif;
    private String nombreLegal;
    private String direccionFiscal;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

