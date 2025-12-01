package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Factura;
import com.academy.academymanager.dto.request.FacturaRequestDto;
import com.academy.academymanager.dto.response.FacturaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Factura entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface FacturaMapper {
    FacturaMapper INSTANCE = Mappers.getMapper(FacturaMapper.class);
    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Factura toEntity(FacturaRequestDto dto);
    @Mapping(target = "idMatricula", source = "matricula.idMatricula")
    FacturaResponseDto toResponseDto(Factura entity);
    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(FacturaRequestDto dto, @MappingTarget Factura entity);
}

