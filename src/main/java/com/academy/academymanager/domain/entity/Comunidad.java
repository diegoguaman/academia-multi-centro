package com.academy.academymanager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing an autonomous community (Comunidad Autónoma).
 */
@Entity
@Table(name = "comunidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comunidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comunidad")
    private Long idComunidad;
    @Column(name = "codigo", unique = true, nullable = false, length = 10)
    private String codigo;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "capital", length = 100)
    private String capital;
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
    @OneToMany(mappedBy = "comunidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Centro> centros;
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

