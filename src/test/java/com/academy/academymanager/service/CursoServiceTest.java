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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CursoService.
 */
@ExtendWith(MockitoExtension.class)
class CursoServiceTest {
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private FormatoRepository formatoRepository;
    @Mock
    private CursoMapper cursoMapper;
    @InjectMocks
    private CursoService cursoService;
    private CursoRequestDto inputCursoRequestDto;
    private Materia mockMateria;
    private Formato mockFormato;
    private Curso mockCurso;
    private CursoResponseDto expectedCursoResponseDto;
    @BeforeEach
    void setUp() {
        mockMateria = Materia.builder()
                .idMateria(1L)
                .nombre("Programming")
                .build();
        mockFormato = Formato.builder()
                .idFormato(1L)
                .nombre("Presencial")
                .build();
        mockCurso = Curso.builder()
                .idCurso(1L)
                .nombre("Java Basics")
                .materia(mockMateria)
                .formato(mockFormato)
                .precioBase(new BigDecimal("500.00"))
                .duracionHoras(40)
                .activo(true)
                .build();
        inputCursoRequestDto = CursoRequestDto.builder()
                .nombre("Java Basics")
                .idMateria(1L)
                .idFormato(1L)
                .precioBase(new BigDecimal("500.00"))
                .duracionHoras(40)
                .build();
        expectedCursoResponseDto = CursoResponseDto.builder()
                .idCurso(1L)
                .nombre("Java Basics")
                .precioBase(new BigDecimal("500.00"))
                .build();
    }
    @Test
    void createShouldReturnCursoResponseDtoWhenValidInput() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(mockMateria));
        when(formatoRepository.findById(1L)).thenReturn(Optional.of(mockFormato));
        when(cursoMapper.toEntity(any(CursoRequestDto.class))).thenReturn(mockCurso);
        when(cursoRepository.save(any(Curso.class))).thenReturn(mockCurso);
        when(cursoMapper.toResponseDto(any(Curso.class))).thenReturn(expectedCursoResponseDto);
        CursoResponseDto actualResult = cursoService.create(inputCursoRequestDto);
        assertNotNull(actualResult);
        assertEquals(expectedCursoResponseDto.getNombre(), actualResult.getNombre());
        verify(materiaRepository).findById(1L);
        verify(formatoRepository).findById(1L);
        verify(cursoRepository).save(any(Curso.class));
    }
    @Test
    void createShouldThrowExceptionWhenMateriaNotFound() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> cursoService.create(inputCursoRequestDto));
        assertEquals("Materia not found with id: 1", exception.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    @Test
    void createShouldThrowExceptionWhenFormatoNotFound() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(mockMateria));
        when(formatoRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> cursoService.create(inputCursoRequestDto));
        assertEquals("Formato not found with id: 1", exception.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    @Test
    void findByIdShouldReturnCursoResponseDtoWhenExists() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(mockCurso));
        when(cursoMapper.toResponseDto(any(Curso.class))).thenReturn(expectedCursoResponseDto);
        CursoResponseDto actualResult = cursoService.findById(1L);
        assertNotNull(actualResult);
        assertEquals(expectedCursoResponseDto.getIdCurso(), actualResult.getIdCurso());
        verify(cursoRepository).findById(1L);
    }
    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> cursoService.findById(1L));
        assertEquals("Curso not found with id: 1", exception.getMessage());
    }
    @Test
    void findAllShouldReturnListOfCursoResponseDto() {
        List<Curso> mockCursos = List.of(mockCurso);
        when(cursoRepository.findAll()).thenReturn(mockCursos);
        when(cursoMapper.toResponseDto(any(Curso.class))).thenReturn(expectedCursoResponseDto);
        List<CursoResponseDto> actualResult = cursoService.findAll();
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        verify(cursoRepository).findAll();
    }
    @Test
    void findActiveShouldReturnListOfActiveCursoResponseDto() {
        List<Curso> mockCursos = List.of(mockCurso);
        when(cursoRepository.findByActivoTrue()).thenReturn(mockCursos);
        when(cursoMapper.toResponseDto(any(Curso.class))).thenReturn(expectedCursoResponseDto);
        List<CursoResponseDto> actualResult = cursoService.findActive();
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        verify(cursoRepository).findByActivoTrue();
    }
    @Test
    void deleteShouldDeleteCursoWhenExists() {
        when(cursoRepository.existsById(1L)).thenReturn(true);
        cursoService.delete(1L);
        verify(cursoRepository).existsById(1L);
        verify(cursoRepository).deleteById(1L);
    }
    @Test
    void deleteShouldThrowExceptionWhenNotFound() {
        when(cursoRepository.existsById(1L)).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> cursoService.delete(1L));
        assertEquals("Curso not found with id: 1", exception.getMessage());
        verify(cursoRepository, never()).deleteById(anyLong());
    }
}

