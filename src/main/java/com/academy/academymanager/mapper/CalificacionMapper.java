package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Calificacion;
import com.academy.academymanager.dto.request.CalificacionRequestDto;
import com.academy.academymanager.dto.response.CalificacionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Calificacion entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CalificacionMapper {
    CalificacionMapper INSTANCE = Mappers.getMapper(CalificacionMapper.class);
    @Mapping(target = "idCalificacion", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Calificacion toEntity(CalificacionRequestDto dto);
    @Mapping(target = "idMatricula", source = "matricula.idMatricula")
    CalificacionResponseDto toResponseDto(Calificacion entity);
    @Mapping(target = "idCalificacion", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(CalificacionRequestDto dto, @MappingTarget Calificacion entity);
}

