package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Formato;
import com.academy.academymanager.dto.request.FormatoRequestDto;
import com.academy.academymanager.dto.response.FormatoResponseDto;
import com.academy.academymanager.mapper.FormatoMapper;
import com.academy.academymanager.repository.FormatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Formato entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FormatoService {
    private final FormatoRepository formatoRepository;
    private final FormatoMapper formatoMapper;
    public FormatoResponseDto create(FormatoRequestDto requestDto) {
        Formato formato = formatoMapper.toEntity(requestDto);
        Formato saved = formatoRepository.save(formato);
        return formatoMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public FormatoResponseDto findById(Long id) {
        Formato formato = formatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formato not found with id: " + id));
        return formatoMapper.toResponseDto(formato);
    }
    @Transactional(readOnly = true)
    public List<FormatoResponseDto> findAll() {
        return formatoRepository.findAll().stream()
                .map(formatoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public FormatoResponseDto update(Long id, FormatoRequestDto requestDto) {
        Formato formato = formatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formato not found with id: " + id));
        formatoMapper.updateEntityFromDto(requestDto, formato);
        Formato updated = formatoRepository.save(formato);
        return formatoMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!formatoRepository.existsById(id)) {
            throw new IllegalArgumentException("Formato not found with id: " + id);
        }
        formatoRepository.deleteById(id);
    }
}

