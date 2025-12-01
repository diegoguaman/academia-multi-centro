package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Curso;
import com.academy.academymanager.domain.entity.Materia;
import com.academy.academymanager.domain.entity.Formato;
import com.academy.academymanager.dto.request.CursoRequestDto;
import com.academy.academymanager.dto.response.CursoResponseDto;
import com.academy.academymanager.mapper.CursoMapper;
import com.academy.academymanager.repository.CursoRepository;
import com.academy.academymanager.repository.MateriaRepository;
import com.academy.academymanager.repository.FormatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Curso entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CursoService {
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final FormatoRepository formatoRepository;
    private final CursoMapper cursoMapper;
    public CursoResponseDto create(CursoRequestDto requestDto) {
        Materia materia = materiaRepository.findById(requestDto.getIdMateria())
                .orElseThrow(() -> new IllegalArgumentException("Materia not found with id: " + requestDto.getIdMateria()));
        Formato formato = formatoRepository.findById(requestDto.getIdFormato())
                .orElseThrow(() -> new IllegalArgumentException("Formato not found with id: " + requestDto.getIdFormato()));
        Curso curso = cursoMapper.toEntity(requestDto);
        curso.setMateria(materia);
        curso.setFormato(formato);
        Curso saved = cursoRepository.save(curso);
        return cursoMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public CursoResponseDto findById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso not found with id: " + id));
        return cursoMapper.toResponseDto(curso);
    }
    @Transactional(readOnly = true)
    public List<CursoResponseDto> findAll() {
        return cursoRepository.findAll().stream()
                .map(cursoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<CursoResponseDto> findActive() {
        return cursoRepository.findByActivoTrue().stream()
                .map(cursoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public CursoResponseDto update(Long id, CursoRequestDto requestDto) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso not found with id: " + id));
        if (requestDto.getIdMateria() != null) {
            Materia materia = materiaRepository.findById(requestDto.getIdMateria())
                    .orElseThrow(() -> new IllegalArgumentException("Materia not found with id: " + requestDto.getIdMateria()));
            curso.setMateria(materia);
        }
        if (requestDto.getIdFormato() != null) {
            Formato formato = formatoRepository.findById(requestDto.getIdFormato())
                    .orElseThrow(() -> new IllegalArgumentException("Formato not found with id: " + requestDto.getIdFormato()));
            curso.setFormato(formato);
        }
        cursoMapper.updateEntityFromDto(requestDto, curso);
        Curso updated = cursoRepository.save(curso);
        return cursoMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Curso not found with id: " + id);
        }
        cursoRepository.deleteById(id);
    }
}

