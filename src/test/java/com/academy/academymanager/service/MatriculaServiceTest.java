package com.academy.academymanager.service;

import com.academy.academymanager.domain.entity.*;
import com.academy.academymanager.dto.request.MatriculaRequestDto;
import com.academy.academymanager.dto.response.MatriculaResponseDto;
import com.academy.academymanager.mapper.MatriculaMapper;
import com.academy.academymanager.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MatriculaService.
 */
@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {
    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private ConvocatoriaRepository convocatoriaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EntidadSubvencionadoraRepository entidadSubvencionadoraRepository;
    @Mock
    private DatosPersonalesRepository datosPersonalesRepository;
    @Mock
    private MatriculaMapper matriculaMapper;
    @InjectMocks
    private MatriculaService matriculaService;
    private MatriculaRequestDto inputMatriculaRequestDto;
    private Convocatoria mockConvocatoria;
    private Usuario mockAlumno;
    private Curso mockCurso;
    private Matricula mockMatricula;
    private MatriculaResponseDto expectedMatriculaResponseDto;
    @BeforeEach
    void setUp() {
        mockCurso = Curso.builder()
                .idCurso(1L)
                .nombre("Java Basics")
                .precioBase(new BigDecimal("500.00"))
                .build();
        mockConvocatoria = Convocatoria.builder()
                .idConvocatoria(1L)
                .codigo("CONV-001")
                .curso(mockCurso)
                .build();
        mockAlumno = Usuario.builder()
                .idUsuario(1L)
                .email("alumno@example.com")
                .rol(Usuario.Rol.ALUMNO)
                .build();
        mockMatricula = Matricula.builder()
                .idMatricula(1L)
                .codigo("MAT-001")
                .convocatoria(mockConvocatoria)
                .alumno(mockAlumno)
                .precioBruto(new BigDecimal("500.00"))
                .descuentoAplicado(BigDecimal.ZERO)
                .importeSubvencionado(BigDecimal.ZERO)
                .estadoPago(Matricula.EstadoPago.PENDIENTE)
                .fechaMatricula(LocalDateTime.now())
                .build();
        inputMatriculaRequestDto = MatriculaRequestDto.builder()
                .idConvocatoria(1L)
                .idAlumno(1L)
                .build();
        expectedMatriculaResponseDto = MatriculaResponseDto.builder()
                .idMatricula(1L)
                .codigo("MAT-001")
                .precioBruto(new BigDecimal("500.00"))
                .build();
    }
    @Test
    void createShouldReturnMatriculaResponseDtoWhenValidInput() {
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockAlumno));
        when(matriculaMapper.toEntity(any(MatriculaRequestDto.class))).thenReturn(mockMatricula);
        when(datosPersonalesRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(mockMatricula);
        when(matriculaMapper.toResponseDto(any(Matricula.class))).thenReturn(expectedMatriculaResponseDto);
        MatriculaResponseDto actualResult = matriculaService.create(inputMatriculaRequestDto);
        assertNotNull(actualResult);
        assertEquals(expectedMatriculaResponseDto.getIdMatricula(), actualResult.getIdMatricula());
        verify(convocatoriaRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(matriculaRepository).save(any(Matricula.class));
    }
    @Test
    void createShouldThrowExceptionWhenConvocatoriaNotFound() {
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> matriculaService.create(inputMatriculaRequestDto));
        assertEquals("Convocatoria not found with id: 1", exception.getMessage());
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }
    @Test
    void createShouldThrowExceptionWhenAlumnoNotFound() {
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> matriculaService.create(inputMatriculaRequestDto));
        assertEquals("Alumno not found with id: 1", exception.getMessage());
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }
    @Test
    void createShouldThrowExceptionWhenUserIsNotAlumno() {
        Usuario profesor = Usuario.builder()
                .idUsuario(2L)
                .rol(Usuario.Rol.PROFESOR)
                .build();
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(profesor));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> matriculaService.create(inputMatriculaRequestDto));
        assertEquals("User is not an ALUMNO: 1", exception.getMessage());
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }
    @Test
    void createShouldApplyDiscountWhenDiscapacidadGreaterThan33() {
        DatosPersonales datosPersonales = DatosPersonales.builder()
                .idUsuario(1L)
                .discapacidadPorcentaje(new BigDecimal("40.00"))
                .build();
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockAlumno));
        when(matriculaMapper.toEntity(any(MatriculaRequestDto.class))).thenReturn(mockMatricula);
        when(datosPersonalesRepository.findById(1L)).thenReturn(Optional.of(datosPersonales));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(mockMatricula);
        when(matriculaMapper.toResponseDto(any(Matricula.class))).thenReturn(expectedMatriculaResponseDto);
        matriculaService.create(inputMatriculaRequestDto);
        verify(matriculaRepository).save(argThat(matricula -> 
                matricula.getDescuentoAplicado().compareTo(BigDecimal.ZERO) > 0 &&
                matricula.getMotivoDescuento().contains("Discapacidad")
        ));
    }
    @Test
    void findByIdShouldReturnMatriculaResponseDtoWhenExists() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(mockMatricula));
        when(matriculaMapper.toResponseDto(any(Matricula.class))).thenReturn(expectedMatriculaResponseDto);
        MatriculaResponseDto actualResult = matriculaService.findById(1L);
        assertNotNull(actualResult);
        assertEquals(expectedMatriculaResponseDto.getIdMatricula(), actualResult.getIdMatricula());
        verify(matriculaRepository).findById(1L);
    }
    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> matriculaService.findById(1L));
        assertEquals("Matricula not found with id: 1", exception.getMessage());
    }
    @Test
    void deleteShouldDeleteMatriculaWhenExists() {
        when(matriculaRepository.existsById(1L)).thenReturn(true);
        matriculaService.delete(1L);
        verify(matriculaRepository).existsById(1L);
        verify(matriculaRepository).deleteById(1L);
    }
    @Test
    void deleteShouldThrowExceptionWhenNotFound() {
        when(matriculaRepository.existsById(1L)).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> matriculaService.delete(1L));
        assertEquals("Matricula not found with id: 1", exception.getMessage());
        verify(matriculaRepository, never()).deleteById(anyLong());
    }
}

