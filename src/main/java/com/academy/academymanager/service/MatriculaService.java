package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.*;
import com.academy.academymanager.dto.request.MatriculaRequestDto;
import com.academy.academymanager.dto.response.MatriculaResponseDto;
import com.academy.academymanager.mapper.MatriculaMapper;
import com.academy.academymanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing Matricula entities.
 * Contains business logic for discounts and pricing.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MatriculaService {
    private final MatriculaRepository matriculaRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntidadSubvencionadoraRepository entidadSubvencionadoraRepository;
    private final DatosPersonalesRepository datosPersonalesRepository;
    private final MatriculaMapper matriculaMapper;
    private static final BigDecimal DISCAPACIDAD_THRESHOLD = new BigDecimal("33.0");
    private static final BigDecimal DISCAPACIDAD_DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    public MatriculaResponseDto create(MatriculaRequestDto requestDto) {
        Convocatoria convocatoria = convocatoriaRepository.findById(requestDto.getIdConvocatoria())
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + requestDto.getIdConvocatoria()));
        Usuario alumno = usuarioRepository.findById(requestDto.getIdAlumno())
                .orElseThrow(() -> new IllegalArgumentException("Alumno not found with id: " + requestDto.getIdAlumno()));
        if (!alumno.getRol().equals(Usuario.Rol.ALUMNO)) {
            throw new IllegalArgumentException("User is not an ALUMNO: " + requestDto.getIdAlumno());
        }
        Matricula matricula = matriculaMapper.toEntity(requestDto);
        matricula.setConvocatoria(convocatoria);
        matricula.setAlumno(alumno);
        if (requestDto.getIdEntidadSubvencionadora() != null) {
            EntidadSubvencionadora entidad = entidadSubvencionadoraRepository.findById(requestDto.getIdEntidadSubvencionadora())
                    .orElseThrow(() -> new IllegalArgumentException("EntidadSubvencionadora not found with id: " + requestDto.getIdEntidadSubvencionadora()));
            matricula.setEntidadSubvencionadora(entidad);
        }
        if (matricula.getCodigo() == null || matricula.getCodigo().isEmpty()) {
            matricula.setCodigo(generateCodigoMatricula());
        }
        calculatePricing(matricula);
        Matricula saved = matriculaRepository.save(matricula);
        return matriculaMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public MatriculaResponseDto findById(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matricula not found with id: " + id));
        return matriculaMapper.toResponseDto(matricula);
    }
    @Transactional(readOnly = true)
    public List<MatriculaResponseDto> findAll() {
        return matriculaRepository.findAll().stream()
                .map(matriculaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<MatriculaResponseDto> findByAlumno(Long idAlumno) {
        return matriculaRepository.findByAlumnoIdUsuario(idAlumno).stream()
                .map(matriculaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public MatriculaResponseDto update(Long id, MatriculaRequestDto requestDto) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matricula not found with id: " + id));
        if (requestDto.getIdConvocatoria() != null) {
            Convocatoria convocatoria = convocatoriaRepository.findById(requestDto.getIdConvocatoria())
                    .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + requestDto.getIdConvocatoria()));
            matricula.setConvocatoria(convocatoria);
        }
        if (requestDto.getIdEntidadSubvencionadora() != null) {
            EntidadSubvencionadora entidad = entidadSubvencionadoraRepository.findById(requestDto.getIdEntidadSubvencionadora())
                    .orElseThrow(() -> new IllegalArgumentException("EntidadSubvencionadora not found with id: " + requestDto.getIdEntidadSubvencionadora()));
            matricula.setEntidadSubvencionadora(entidad);
        }
        if (requestDto.getImporteSubvencionado() != null) {
            matricula.setImporteSubvencionado(requestDto.getImporteSubvencionado());
        }
        matriculaMapper.updateEntityFromDto(requestDto, matricula);
        calculatePricing(matricula);
        Matricula updated = matriculaRepository.save(matricula);
        return matriculaMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!matriculaRepository.existsById(id)) {
            throw new IllegalArgumentException("Matricula not found with id: " + id);
        }
        matriculaRepository.deleteById(id);
    }
    private void calculatePricing(Matricula matricula) {
        BigDecimal precioBase = matricula.getConvocatoria().getCurso().getPrecioBase();
        matricula.setPrecioBruto(precioBase);
        datosPersonalesRepository.findById(matricula.getAlumno().getIdUsuario())
                .ifPresent(datosPersonales -> {
                    BigDecimal discapacidadPorcentaje = datosPersonales.getDiscapacidadPorcentaje();
                    if (discapacidadPorcentaje != null && discapacidadPorcentaje.compareTo(DISCAPACIDAD_THRESHOLD) >= 0) {
                        BigDecimal descuento = precioBase.multiply(DISCAPACIDAD_DISCOUNT_PERCENTAGE);
                        matricula.setDescuentoAplicado(descuento);
                        matricula.setMotivoDescuento("Descuento Discapacidad (+33%)");
                    } else {
                        matricula.setDescuentoAplicado(BigDecimal.ZERO);
                        matricula.setMotivoDescuento("Sin descuento");
                    }
                });
        if (matricula.getImporteSubvencionado() == null) {
            matricula.setImporteSubvencionado(BigDecimal.ZERO);
        }
    }
    private String generateCodigoMatricula() {
        return "MAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

