package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Empresa;
import com.academy.academymanager.dto.request.EmpresaRequestDto;
import com.academy.academymanager.dto.response.EmpresaResponseDto;
import com.academy.academymanager.mapper.EmpresaMapper;
import com.academy.academymanager.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Empresa entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    public EmpresaResponseDto create(EmpresaRequestDto requestDto) {
        if (empresaRepository.existsByCif(requestDto.getCif())) {
            throw new IllegalArgumentException("CIF already exists: " + requestDto.getCif());
        }
        Empresa empresa = empresaMapper.toEntity(requestDto);
        Empresa saved = empresaRepository.save(empresa);
        return empresaMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public EmpresaResponseDto findById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa not found with id: " + id));
        return empresaMapper.toResponseDto(empresa);
    }
    @Transactional(readOnly = true)
    public List<EmpresaResponseDto> findAll() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public EmpresaResponseDto update(Long id, EmpresaRequestDto requestDto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa not found with id: " + id));
        if (!empresa.getCif().equals(requestDto.getCif()) && empresaRepository.existsByCif(requestDto.getCif())) {
            throw new IllegalArgumentException("CIF already exists: " + requestDto.getCif());
        }
        empresaMapper.updateEntityFromDto(requestDto, empresa);
        Empresa updated = empresaRepository.save(empresa);
        return empresaMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new IllegalArgumentException("Empresa not found with id: " + id);
        }
        empresaRepository.deleteById(id);
    }
}

