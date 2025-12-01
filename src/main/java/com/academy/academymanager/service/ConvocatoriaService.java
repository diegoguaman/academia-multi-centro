package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Convocatoria;
import com.academy.academymanager.domain.entity.Curso;
import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.domain.entity.Centro;
import com.academy.academymanager.dto.request.ConvocatoriaRequestDto;
import com.academy.academymanager.dto.response.ConvocatoriaResponseDto;
import com.academy.academymanager.mapper.ConvocatoriaMapper;
import com.academy.academymanager.repository.ConvocatoriaRepository;
import com.academy.academymanager.repository.CursoRepository;
import com.academy.academymanager.repository.UsuarioRepository;
import com.academy.academymanager.repository.CentroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing Convocatoria entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConvocatoriaService {
    private final ConvocatoriaRepository convocatoriaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CentroRepository centroRepository;
    private final ConvocatoriaMapper convocatoriaMapper;
    public ConvocatoriaResponseDto create(ConvocatoriaRequestDto requestDto) {
        validateDates(requestDto.getFechaInicio(), requestDto.getFechaFin());
        Curso curso = cursoRepository.findById(requestDto.getIdCurso())
                .orElseThrow(() -> new IllegalArgumentException("Curso not found with id: " + requestDto.getIdCurso()));
        Usuario profesor = usuarioRepository.findById(requestDto.getIdProfesor())
                .orElseThrow(() -> new IllegalArgumentException("Profesor not found with id: " + requestDto.getIdProfesor()));
        if (!profesor.getRol().equals(Usuario.Rol.PROFESOR)) {
            throw new IllegalArgumentException("User is not a PROFESOR: " + requestDto.getIdProfesor());
        }
        Centro centro = centroRepository.findById(requestDto.getIdCentro())
                .orElseThrow(() -> new IllegalArgumentException("Centro not found with id: " + requestDto.getIdCentro()));
        Convocatoria convocatoria = convocatoriaMapper.toEntity(requestDto);
        convocatoria.setCurso(curso);
        convocatoria.setProfesor(profesor);
        convocatoria.setCentro(centro);
        if (convocatoria.getCodigo() == null || convocatoria.getCodigo().isEmpty()) {
            convocatoria.setCodigo(generateCodigoConvocatoria());
        }
        Convocatoria saved = convocatoriaRepository.save(convocatoria);
        return convocatoriaMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public ConvocatoriaResponseDto findById(Long id) {
        Convocatoria convocatoria = convocatoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + id));
        return convocatoriaMapper.toResponseDto(convocatoria);
    }
    @Transactional(readOnly = true)
    public List<ConvocatoriaResponseDto> findAll() {
        return convocatoriaRepository.findAll().stream()
                .map(convocatoriaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<ConvocatoriaResponseDto> findActive() {
        return convocatoriaRepository.findByActivoTrue().stream()
                .map(convocatoriaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public ConvocatoriaResponseDto update(Long id, ConvocatoriaRequestDto requestDto) {
        Convocatoria convocatoria = convocatoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + id));
        validateDates(requestDto.getFechaInicio(), requestDto.getFechaFin());
        if (requestDto.getIdCurso() != null) {
            Curso curso = cursoRepository.findById(requestDto.getIdCurso())
                    .orElseThrow(() -> new IllegalArgumentException("Curso not found with id: " + requestDto.getIdCurso()));
            convocatoria.setCurso(curso);
        }
        if (requestDto.getIdProfesor() != null) {
            Usuario profesor = usuarioRepository.findById(requestDto.getIdProfesor())
                    .orElseThrow(() -> new IllegalArgumentException("Profesor not found with id: " + requestDto.getIdProfesor()));
            if (!profesor.getRol().equals(Usuario.Rol.PROFESOR)) {
                throw new IllegalArgumentException("User is not a PROFESOR: " + requestDto.getIdProfesor());
            }
            convocatoria.setProfesor(profesor);
        }
        if (requestDto.getIdCentro() != null) {
            Centro centro = centroRepository.findById(requestDto.getIdCentro())
                    .orElseThrow(() -> new IllegalArgumentException("Centro not found with id: " + requestDto.getIdCentro()));
            convocatoria.setCentro(centro);
        }
        convocatoriaMapper.updateEntityFromDto(requestDto, convocatoria);
        Convocatoria updated = convocatoriaRepository.save(convocatoria);
        return convocatoriaMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!convocatoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Convocatoria not found with id: " + id);
        }
        convocatoriaRepository.deleteById(id);
    }
    private void validateDates(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
    private String generateCodigoConvocatoria() {
        return "CONV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

