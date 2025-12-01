package com.academy.academymanager.dto.response;

import com.academy.academymanager.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Usuario response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {
    private Long idUsuario;
    private String email;
    private Usuario.Rol rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

