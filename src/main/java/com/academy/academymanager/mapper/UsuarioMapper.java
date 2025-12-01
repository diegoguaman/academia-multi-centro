package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.request.UsuarioRequestDto;
import com.academy.academymanager.dto.response.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Usuario entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "datosPersonales", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Usuario toEntity(UsuarioRequestDto dto);
    UsuarioResponseDto toResponseDto(Usuario entity);
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "datosPersonales", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntityFromDto(UsuarioRequestDto dto, @MappingTarget Usuario entity);
}

