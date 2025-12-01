package com.academy.academymanager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a center (academy location).
 */
@Entity
@Table(name = "centro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Centro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_centro")
    private Long idCentro;
    @Column(name = "codigo_centro", unique = true, nullable = false, length = 20)
    private String codigoCentro;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comunidad", nullable = false)
    private Comunidad comunidad;
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;
    @OneToMany(mappedBy = "centro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Convocatoria> convocatorias;
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}

