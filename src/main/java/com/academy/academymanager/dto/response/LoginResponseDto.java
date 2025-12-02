package com.academy.academymanager.dto.response;

import com.academy.academymanager.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response containing JWT token and user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private String email;
    private Usuario.Rol rol;
    private String nombre;
}

