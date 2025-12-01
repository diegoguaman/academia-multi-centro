package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Materia;
import com.academy.academymanager.dto.request.MateriaRequestDto;
import com.academy.academymanager.dto.response.MateriaResponseDto;
import com.academy.academymanager.mapper.MateriaMapper;
import com.academy.academymanager.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Materia entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MateriaService {
    private final MateriaRepository materiaRepository;
    private final MateriaMapper materiaMapper;
    public MateriaResponseDto create(MateriaRequestDto requestDto) {
        Materia materia = materiaMapper.toEntity(requestDto);
        Materia saved = materiaRepository.save(materia);
        return materiaMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public MateriaResponseDto findById(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia not found with id: " + id));
        return materiaMapper.toResponseDto(materia);
    }
    @Transactional(readOnly = true)
    public List<MateriaResponseDto> findAll() {
        return materiaRepository.findAll().stream()
                .map(materiaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<MateriaResponseDto> findActive() {
        return materiaRepository.findByActivoTrue().stream()
                .map(materiaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public MateriaResponseDto update(Long id, MateriaRequestDto requestDto) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia not found with id: " + id));
        materiaMapper.updateEntityFromDto(requestDto, materia);
        Materia updated = materiaRepository.save(materia);
        return materiaMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new IllegalArgumentException("Materia not found with id: " + id);
        }
        materiaRepository.deleteById(id);
    }
}

