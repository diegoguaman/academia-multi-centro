package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Centro;
import com.academy.academymanager.domain.entity.Empresa;
import com.academy.academymanager.domain.entity.Comunidad;
import com.academy.academymanager.dto.request.CentroRequestDto;
import com.academy.academymanager.dto.response.CentroResponseDto;
import com.academy.academymanager.mapper.CentroMapper;
import com.academy.academymanager.repository.CentroRepository;
import com.academy.academymanager.repository.EmpresaRepository;
import com.academy.academymanager.repository.ComunidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Centro entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CentroService {
    private final CentroRepository centroRepository;
    private final EmpresaRepository empresaRepository;
    private final ComunidadRepository comunidadRepository;
    private final CentroMapper centroMapper;
    public CentroResponseDto create(CentroRequestDto requestDto) {
        Empresa empresa = empresaRepository.findById(requestDto.getIdEmpresa())
                .orElseThrow(() -> new IllegalArgumentException("Empresa not found with id: " + requestDto.getIdEmpresa()));
        Comunidad comunidad = comunidadRepository.findById(requestDto.getIdComunidad())
                .orElseThrow(() -> new IllegalArgumentException("Comunidad not found with id: " + requestDto.getIdComunidad()));
        Centro centro = centroMapper.toEntity(requestDto);
        centro.setEmpresa(empresa);
        centro.setComunidad(comunidad);
        Centro saved = centroRepository.save(centro);
        return centroMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public CentroResponseDto findById(Long id) {
        Centro centro = centroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro not found with id: " + id));
        return centroMapper.toResponseDto(centro);
    }
    @Transactional(readOnly = true)
    public List<CentroResponseDto> findAll() {
        return centroRepository.findAll().stream()
                .map(centroMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<CentroResponseDto> findActive() {
        return centroRepository.findByActivoTrue().stream()
                .map(centroMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public CentroResponseDto update(Long id, CentroRequestDto requestDto) {
        Centro centro = centroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro not found with id: " + id));
        if (requestDto.getIdEmpresa() != null) {
            Empresa empresa = empresaRepository.findById(requestDto.getIdEmpresa())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa not found with id: " + requestDto.getIdEmpresa()));
            centro.setEmpresa(empresa);
        }
        if (requestDto.getIdComunidad() != null) {
            Comunidad comunidad = comunidadRepository.findById(requestDto.getIdComunidad())
                    .orElseThrow(() -> new IllegalArgumentException("Comunidad not found with id: " + requestDto.getIdComunidad()));
            centro.setComunidad(comunidad);
        }
        centroMapper.updateEntityFromDto(requestDto, centro);
        Centro updated = centroRepository.save(centro);
        return centroMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!centroRepository.existsById(id)) {
            throw new IllegalArgumentException("Centro not found with id: " + id);
        }
        centroRepository.deleteById(id);
    }
}

