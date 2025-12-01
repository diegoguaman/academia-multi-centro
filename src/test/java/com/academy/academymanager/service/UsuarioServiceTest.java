package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.Usuario;
import com.academy.academymanager.dto.request.UsuarioRequestDto;
import com.academy.academymanager.dto.response.UsuarioResponseDto;
import com.academy.academymanager.mapper.UsuarioMapper;
import com.academy.academymanager.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UsuarioService.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @InjectMocks
    private UsuarioService usuarioService;
    private UsuarioRequestDto inputUsuarioRequestDto;
    private Usuario mockUsuario;
    private UsuarioResponseDto expectedUsuarioResponseDto;
    @BeforeEach
    void setUp() {
        inputUsuarioRequestDto = UsuarioRequestDto.builder()
                .email("test@example.com")
                .password("password123")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .build();
        mockUsuario = Usuario.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        expectedUsuarioResponseDto = UsuarioResponseDto.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
    @Test
    void createShouldReturnUsuarioResponseDtoWhenValidInput() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioMapper.toEntity(any(UsuarioRequestDto.class))).thenReturn(mockUsuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        UsuarioResponseDto actualResult = usuarioService.create(inputUsuarioRequestDto);
        assertNotNull(actualResult);
        assertEquals(expectedUsuarioResponseDto.getEmail(), actualResult.getEmail());
        verify(usuarioRepository).existsByEmail(inputUsuarioRequestDto.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }
    @Test
    void createShouldThrowExceptionWhenEmailExists() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.create(inputUsuarioRequestDto));
        assertEquals("Email already exists: " + inputUsuarioRequestDto.getEmail(), exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    void findByIdShouldReturnUsuarioResponseDtoWhenExists() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        UsuarioResponseDto actualResult = usuarioService.findById(1L);
        assertNotNull(actualResult);
        assertEquals(expectedUsuarioResponseDto.getIdUsuario(), actualResult.getIdUsuario());
        verify(usuarioRepository).findById(1L);
    }
    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.findById(1L));
        assertEquals("Usuario not found with id: 1", exception.getMessage());
    }
    @Test
    void findAllShouldReturnListOfUsuarioResponseDto() {
        List<Usuario> mockUsuarios = List.of(mockUsuario);
        when(usuarioRepository.findAll()).thenReturn(mockUsuarios);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        List<UsuarioResponseDto> actualResult = usuarioService.findAll();
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        verify(usuarioRepository).findAll();
    }
    @Test
    void updateShouldReturnUpdatedUsuarioResponseDtoWhenValidInput() {
        UsuarioRequestDto updateRequestDto = UsuarioRequestDto.builder()
                .email("updated@example.com")
                .rol(Usuario.Rol.PROFESOR)
                .activo(false)
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(usuarioRepository.existsByEmailAndIdUsuarioNot(anyString(), anyLong())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        UsuarioResponseDto actualResult = usuarioService.update(1L, updateRequestDto);
        assertNotNull(actualResult);
        verify(usuarioRepository).findById(1L);
        verify(usuarioMapper).updateEntityFromDto(updateRequestDto, mockUsuario);
        verify(usuarioRepository).save(mockUsuario);
    }
    @Test
    void updateShouldThrowExceptionWhenEmailExistsForAnotherUser() {
        UsuarioRequestDto updateRequestDto = UsuarioRequestDto.builder()
                .email("existing@example.com")
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(usuarioRepository.existsByEmailAndIdUsuarioNot(anyString(), anyLong())).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.update(1L, updateRequestDto));
        assertEquals("Email already exists: " + updateRequestDto.getEmail(), exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    void deleteShouldDeleteUsuarioWhenExists() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        usuarioService.delete(1L);
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }
    @Test
    void deleteShouldThrowExceptionWhenNotFound() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.delete(1L));
        assertEquals("Usuario not found with id: 1", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}

