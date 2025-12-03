package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.request.UsuarioRequestDto;
import com.academy.academymanager.dto.response.UsuarioResponseDto;
import com.academy.academymanager.mapper.UsuarioMapper;
import com.academy.academymanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Usuario entities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    public UsuarioResponseDto create(UsuarioRequestDto requestDto) {
        if (usuarioRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + requestDto.getEmail());
        }
        Usuario usuario = usuarioMapper.toEntity(requestDto);
        usuario.setPasswordHash(hashPassword(requestDto.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(saved);
    }
    @Transactional(readOnly = true)
    public UsuarioResponseDto findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario not found with id: " + id));
        return usuarioMapper.toResponseDto(usuario);
    }
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> findByRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public UsuarioResponseDto update(Long id, UsuarioRequestDto requestDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario not found with id: " + id));
        if (usuarioRepository.existsByEmailAndIdUsuarioNot(requestDto.getEmail(), id)) {
            throw new IllegalArgumentException("Email already exists: " + requestDto.getEmail());
        }
        usuarioMapper.updateEntityFromDto(requestDto, usuario);
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            usuario.setPasswordHash(hashPassword(requestDto.getPassword()));
        }
        Usuario updated = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(updated);
    }
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario not found with id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    private String hashPassword(String password) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}

