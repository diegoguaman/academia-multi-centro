package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Curso;
import com.academy.academymanager.domain.entity.Materia;
import com.academy.academymanager.domain.entity.Formato;
import com.academy.academymanager.repository.CursoRepository;
import com.academy.academymanager.repository.MateriaRepository;
import com.academy.academymanager.repository.FormatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CursoRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
class CursoRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private FormatoRepository formatoRepository;
    private Materia testMateria;
    private Formato testFormato;
    private Curso testCurso;
    @BeforeEach
    void setUp() {
        testMateria = Materia.builder()
                .nombre("Programming")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(testMateria);
        testFormato = Formato.builder()
                .nombre("Presencial")
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(testFormato);
        testCurso = Curso.builder()
                .nombre("Java Basics")
                .materia(testMateria)
                .formato(testFormato)
                .precioBase(new BigDecimal("500.00"))
                .duracionHoras(40)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(testCurso);
    }
    @Test
    void findByActivoTrueShouldReturnOnlyActiveCursos() {
        Curso inactiveCurso = Curso.builder()
                .nombre("Inactive Course")
                .materia(testMateria)
                .formato(testFormato)
                .precioBase(new BigDecimal("300.00"))
                .activo(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(inactiveCurso);
        List<Curso> activeCursos = cursoRepository.findByActivoTrue();
        assertFalse(activeCursos.isEmpty());
        assertTrue(activeCursos.stream().allMatch(Curso::getActivo));
    }
    @Test
    void findByMateriaIdMateriaShouldReturnCursosForMateria() {
        List<Curso> cursos = cursoRepository.findByMateriaIdMateria(testMateria.getIdMateria());
        assertFalse(cursos.isEmpty());
        assertTrue(cursos.stream().allMatch(c -> c.getMateria().getIdMateria().equals(testMateria.getIdMateria())));
    }
    @Test
    void saveShouldPersistCurso() {
        Curso newCurso = Curso.builder()
                .nombre("New Course")
                .materia(testMateria)
                .formato(testFormato)
                .precioBase(new BigDecimal("400.00"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        Curso saved = cursoRepository.save(newCurso);
        assertNotNull(saved.getIdCurso());
        assertTrue(cursoRepository.existsById(saved.getIdCurso()));
    }
}

