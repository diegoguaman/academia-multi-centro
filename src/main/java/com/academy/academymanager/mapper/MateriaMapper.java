package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Materia;
import com.academy.academymanager.dto.request.MateriaRequestDto;
import com.academy.academymanager.dto.response.MateriaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Materia entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface MateriaMapper {
    MateriaMapper INSTANCE = Mappers.getMapper(MateriaMapper.class);
    @Mapping(target = "idMateria", ignore = true)
    @Mapping(target = "cursos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Materia toEntity(MateriaRequestDto dto);
    MateriaResponseDto toResponseDto(Materia entity);
    @Mapping(target = "idMateria", ignore = true)
    @Mapping(target = "cursos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(MateriaRequestDto dto, @MappingTarget Materia entity);
}

