package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Empresa;
import com.academy.academymanager.dto.request.EmpresaRequestDto;
import com.academy.academymanager.dto.response.EmpresaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Empresa entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface EmpresaMapper {
    EmpresaMapper INSTANCE = Mappers.getMapper(EmpresaMapper.class);
    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "centros", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Empresa toEntity(EmpresaRequestDto dto);
    EmpresaResponseDto toResponseDto(Empresa entity);
    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "centros", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(EmpresaRequestDto dto, @MappingTarget Empresa entity);
}

