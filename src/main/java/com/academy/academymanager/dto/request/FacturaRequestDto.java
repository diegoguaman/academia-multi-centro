package com.academy.academymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating a Factura.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaRequestDto {
    @NotBlank(message = "Invoice number is required")
    private String numeroFactura;
    @NotNull(message = "Matricula ID is required")
    private Long idMatricula;
    @NotNull(message = "Base amount is required")
    @PositiveOrZero(message = "Base amount must be positive or zero")
    private BigDecimal baseImponible;
    @PositiveOrZero(message = "IVA percentage must be positive or zero")
    private BigDecimal ivaPorcentaje;
    @NotNull(message = "Total is required")
    @PositiveOrZero(message = "Total must be positive or zero")
    private BigDecimal total;
    private String datosFiscalesCliente;
}

