package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.DatosPersonales;
import com.academy.academymanager.dto.request.DatosPersonalesRequestDto;
import com.academy.academymanager.dto.response.DatosPersonalesResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for DatosPersonales entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface DatosPersonalesMapper {
    DatosPersonalesMapper INSTANCE = Mappers.getMapper(DatosPersonalesMapper.class);
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    DatosPersonales toEntity(DatosPersonalesRequestDto dto);
    DatosPersonalesResponseDto toResponseDto(DatosPersonales entity);
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(DatosPersonalesRequestDto dto, @MappingTarget DatosPersonales entity);
}

