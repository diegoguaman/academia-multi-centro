package com.academy.academymanager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing personal data of a user.
 * One-to-one relationship with Usuario.
 */
@Entity
@Table(name = "datos_personales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPersonales {
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    @Column(name = "dni_nie", unique = true, length = 20)
    private String dniNie;
    @Column(name = "telefono", length = 20)
    private String telefono;
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
    @Column(name = "discapacidad_porcentaje", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discapacidadPorcentaje = BigDecimal.ZERO;
    @Column(name = "es_familia_numerosa", nullable = false)
    @Builder.Default
    private Boolean esFamiliaNumerosa = false;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    @Column(name = "updated_by")
    private Long updatedBy;
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}

