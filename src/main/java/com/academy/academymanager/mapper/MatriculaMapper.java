package com.academy.academymanager.mapper;

import com.academy.academymanager.domain.entity.Matricula;
import com.academy.academymanager.dto.request.MatriculaRequestDto;
import com.academy.academymanager.dto.response.MatriculaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for Matricula entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    MatriculaMapper INSTANCE = Mappers.getMapper(MatriculaMapper.class);
    @Mapping(target = "idMatricula", ignore = true)
    @Mapping(target = "convocatoria", ignore = true)
    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "entidadSubvencionadora", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "fechaMatricula", ignore = true)
    @Mapping(target = "precioBruto", ignore = true)
    @Mapping(target = "descuentoAplicado", ignore = true)
    @Mapping(target = "motivoDescuento", ignore = true)
    @Mapping(target = "precioFinal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Matricula toEntity(MatriculaRequestDto dto);
    @Mapping(target = "codigoConvocatoria", source = "convocatoria.codigo")
    @Mapping(target = "nombreAlumno", expression = "java(entity.getAlumno().getDatosPersonales() != null ? entity.getAlumno().getDatosPersonales().getNombre() + \" \" + entity.getAlumno().getDatosPersonales().getApellidos() : null)")
    MatriculaResponseDto toResponseDto(Matricula entity);
    @Mapping(target = "idMatricula", ignore = true)
    @Mapping(target = "convocatoria", ignore = true)
    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "entidadSubvencionadora", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "fechaMatricula", ignore = true)
    @Mapping(target = "precioBruto", ignore = true)
    @Mapping(target = "descuentoAplicado", ignore = true)
    @Mapping(target = "motivoDescuento", ignore = true)
    @Mapping(target = "precioFinal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(MatriculaRequestDto dto, @MappingTarget Matricula entity);
}

