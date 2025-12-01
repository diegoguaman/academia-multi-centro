package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Formato;
import com.academy.academymanager.dto.request.FormatoRequestDto;
import com.academy.academymanager.dto.response.FormatoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Formato entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface FormatoMapper {
    FormatoMapper INSTANCE = Mappers.getMapper(FormatoMapper.class);
    @Mapping(target = "idFormato", ignore = true)
    @Mapping(target = "cursos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Formato toEntity(FormatoRequestDto dto);
    FormatoResponseDto toResponseDto(Formato entity);
    @Mapping(target = "idFormato", ignore = true)
    @Mapping(target = "cursos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(FormatoRequestDto dto, @MappingTarget Formato entity);
}

