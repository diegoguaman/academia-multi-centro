package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Curso;
import com.academy.academymanager.dto.request.CursoRequestDto;
import com.academy.academymanager.dto.response.CursoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Curso entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CursoMapper {
    CursoMapper INSTANCE = Mappers.getMapper(CursoMapper.class);
    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "materia", ignore = true)
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "convocatorias", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Curso toEntity(CursoRequestDto dto);
    @Mapping(target = "idMateria", source = "materia.idMateria")
    @Mapping(target = "nombreMateria", source = "materia.nombre")
    @Mapping(target = "idFormato", source = "formato.idFormato")
    @Mapping(target = "nombreFormato", source = "formato.nombre")
    CursoResponseDto toResponseDto(Curso entity);
    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "materia", ignore = true)
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "convocatorias", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(CursoRequestDto dto, @MappingTarget Curso entity);
}

