package com.academy.academymanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Factura response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaResponseDto {
    private Long idFactura;
    private String numeroFactura;
    private Long idMatricula;
    private LocalDateTime fechaEmision;
    private BigDecimal baseImponible;
    private BigDecimal ivaPorcentaje;
    private BigDecimal total;
    private String datosFiscalesCliente;
    private LocalDateTime fechaCreacion;
}

