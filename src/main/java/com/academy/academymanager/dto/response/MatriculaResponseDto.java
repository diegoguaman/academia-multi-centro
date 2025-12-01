package com.academy.academymanager.dto.response;

import com.academy.academymanager.domain.entity.Matricula;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Matricula response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaResponseDto {
    private Long idMatricula;
    private String codigo;
    private Long idConvocatoria;
    private String codigoConvocatoria;
    private Long idAlumno;
    private String nombreAlumno;
    private LocalDateTime fechaMatricula;
    private BigDecimal precioBruto;
    private BigDecimal descuentoAplicado;
    private String motivoDescuento;
    private Long idEntidadSubvencionadora;
    private BigDecimal importeSubvencionado;
    private BigDecimal precioFinal;
    private Matricula.EstadoPago estadoPago;
    private LocalDateTime fechaCreacion;
}

