package com.academy.academymanager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a student enrollment (matrícula).
 * Contains business logic for discounts and subsidies.
 */
@Entity
@Table(name = "matricula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long idMatricula;
    @Column(name = "codigo", unique = true, length = 50)
    private String codigo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_convocatoria", nullable = false)
    private Convocatoria convocatoria;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Usuario alumno;
    @Column(name = "fecha_matricula", nullable = false, updatable = false)
    private LocalDateTime fechaMatricula;
    @Column(name = "precio_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBruto;
    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    @Column(name = "motivo_descuento", length = 100)
    private String motivoDescuento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entidad_subvencionadora")
    private EntidadSubvencionadora entidadSubvencionadora;
    @Column(name = "importe_subvencionado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal importeSubvencionado = BigDecimal.ZERO;
    @Column(name = "precio_final", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal precioFinal;
    @Column(name = "estado_pago", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Factura> facturas;
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Calificacion> calificaciones;
    public enum EstadoPago {
        PENDIENTE, PAGADO, CANCELADO
    }
    @PrePersist
    protected void onCreate() {
        if (fechaMatricula == null) {
            fechaMatricula = LocalDateTime.now();
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estadoPago == null) {
            estadoPago = EstadoPago.PENDIENTE;
        }
        if (descuentoAplicado == null) {
            descuentoAplicado = BigDecimal.ZERO;
        }
        if (importeSubvencionado == null) {
            importeSubvencionado = BigDecimal.ZERO;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}

