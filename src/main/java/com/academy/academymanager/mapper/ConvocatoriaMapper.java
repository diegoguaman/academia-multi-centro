package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Convocatoria;
import com.academy.academymanager.dto.request.ConvocatoriaRequestDto;
import com.academy.academymanager.dto.response.ConvocatoriaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Convocatoria entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ConvocatoriaMapper {
    ConvocatoriaMapper INSTANCE = Mappers.getMapper(ConvocatoriaMapper.class);
    @Mapping(target = "idConvocatoria", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "centro", ignore = true)
    @Mapping(target = "matriculas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Convocatoria toEntity(ConvocatoriaRequestDto dto);
    @Mapping(target = "idCurso", source = "curso.idCurso")
    @Mapping(target = "nombreCurso", source = "curso.nombre")
    @Mapping(target = "idProfesor", source = "profesor.idUsuario")
    @Mapping(target = "idCentro", source = "centro.idCentro")
    @Mapping(target = "nombreCentro", source = "centro.nombre")
    ConvocatoriaResponseDto toResponseDto(Convocatoria entity);
    @Mapping(target = "idConvocatoria", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "centro", ignore = true)
    @Mapping(target = "matriculas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(ConvocatoriaRequestDto dto, @MappingTarget Convocatoria entity);
}

