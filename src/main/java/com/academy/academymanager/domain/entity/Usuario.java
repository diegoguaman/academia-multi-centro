package com.academy.academymanager.domain.entity;

import com.academy.academymanager.usertype.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 * Supports roles: ADMIN, PROFESOR, ALUMNO
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
    @Type(PostgreSQLEnumType.class)
    private Rol rol;
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DatosPersonales datosPersonales;
    public enum Rol {
        ADMIN, PROFESOR, ALUMNO, ADMINISTRATIVO
    }
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}

