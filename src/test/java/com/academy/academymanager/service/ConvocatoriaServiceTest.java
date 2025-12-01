package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.*;
import com.academy.academymanager.dto.request.ConvocatoriaRequestDto;
import com.academy.academymanager.dto.response.ConvocatoriaResponseDto;
import com.academy.academymanager.mapper.ConvocatoriaMapper;
import com.academy.academymanager.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConvocatoriaService.
 */
@ExtendWith(MockitoExtension.class)
class ConvocatoriaServiceTest {
    @Mock
    private ConvocatoriaRepository convocatoriaRepository;
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CentroRepository centroRepository;
    @Mock
    private ConvocatoriaMapper convocatoriaMapper;
    @InjectMocks
    private ConvocatoriaService convocatoriaService;
    private ConvocatoriaRequestDto inputConvocatoriaRequestDto;
    private Curso mockCurso;
    private Usuario mockProfesor;
    private Centro mockCentro;
    private Convocatoria mockConvocatoria;
    @BeforeEach
    void setUp() {
        mockCurso = Curso.builder()
                .idCurso(1L)
                .nombre("Java Basics")
                .build();
        mockProfesor = Usuario.builder()
                .idUsuario(1L)
                .rol(Usuario.Rol.PROFESOR)
                .build();
        mockCentro = Centro.builder()
                .idCentro(1L)
                .nombre("Centro Madrid")
                .build();
        mockConvocatoria = Convocatoria.builder()
                .idConvocatoria(1L)
                .codigo("CONV-001")
                .curso(mockCurso)
                .profesor(mockProfesor)
                .centro(mockCentro)
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(30))
                .activo(true)
                .build();
        inputConvocatoriaRequestDto = ConvocatoriaRequestDto.builder()
                .idCurso(1L)
                .idProfesor(1L)
                .idCentro(1L)
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(30))
                .build();
    }
    @Test
    void createShouldReturnConvocatoriaResponseDtoWhenValidInput() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(mockCurso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockProfesor));
        when(centroRepository.findById(1L)).thenReturn(Optional.of(mockCentro));
        when(convocatoriaMapper.toEntity(any(ConvocatoriaRequestDto.class))).thenReturn(mockConvocatoria);
        when(convocatoriaRepository.save(any(Convocatoria.class))).thenReturn(mockConvocatoria);
        when(convocatoriaMapper.toResponseDto(any(Convocatoria.class))).thenReturn(ConvocatoriaResponseDto.builder().build());
        ConvocatoriaResponseDto actualResult = convocatoriaService.create(inputConvocatoriaRequestDto);
        assertNotNull(actualResult);
        verify(cursoRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(centroRepository).findById(1L);
        verify(convocatoriaRepository).save(any(Convocatoria.class));
    }
    @Test
    void createShouldThrowExceptionWhenFechaFinBeforeFechaInicio() {
        ConvocatoriaRequestDto invalidRequest = ConvocatoriaRequestDto.builder()
                .fechaInicio(LocalDate.now().plusDays(30))
                .fechaFin(LocalDate.now().plusDays(1))
                .build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> convocatoriaService.create(invalidRequest));
        assertTrue(exception.getMessage().contains("End date must be after start date"));
        verify(convocatoriaRepository, never()).save(any(Convocatoria.class));
    }
    @Test
    void createShouldThrowExceptionWhenUserIsNotProfesor() {
        Usuario alumno = Usuario.builder()
                .idUsuario(2L)
                .rol(Usuario.Rol.ALUMNO)
                .build();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(mockCurso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(alumno));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> convocatoriaService.create(inputConvocatoriaRequestDto));
        assertEquals("User is not a PROFESOR: 1", exception.getMessage());
        verify(convocatoriaRepository, never()).save(any(Convocatoria.class));
    }
    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> convocatoriaService.findById(1L));
        assertEquals("Convocatoria not found with id: 1", exception.getMessage());
    }
    @Test
    void deleteShouldDeleteConvocatoriaWhenExists() {
        when(convocatoriaRepository.existsById(1L)).thenReturn(true);
        convocatoriaService.delete(1L);
        verify(convocatoriaRepository).existsById(1L);
        verify(convocatoriaRepository).deleteById(1L);
    }
}

