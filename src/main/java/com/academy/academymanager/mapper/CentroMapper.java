package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Centro;
import com.academy.academymanager.dto.request.CentroRequestDto;
import com.academy.academymanager.dto.response.CentroResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Centro entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CentroMapper {
    CentroMapper INSTANCE = Mappers.getMapper(CentroMapper.class);
    @Mapping(target = "idCentro", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "comunidad", ignore = true)
    @Mapping(target = "convocatorias", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Centro toEntity(CentroRequestDto dto);
    @Mapping(target = "idEmpresa", source = "empresa.idEmpresa")
    @Mapping(target = "nombreEmpresa", source = "empresa.nombreLegal")
    @Mapping(target = "idComunidad", source = "comunidad.idComunidad")
    @Mapping(target = "nombreComunidad", source = "comunidad.nombre")
    CentroResponseDto toResponseDto(Centro entity);
    @Mapping(target = "idCentro", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "comunidad", ignore = true)
    @Mapping(target = "convocatorias", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(CentroRequestDto dto, @MappingTarget Centro entity);
}

