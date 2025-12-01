package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Convocatoria entity.
 */
@Repository
public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
    Optional<Convocatoria> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Convocatoria> findByActivoTrue();
    List<Convocatoria> findByCursoIdCurso(Long idCurso);
    List<Convocatoria> findByProfesorIdUsuario(Long idProfesor);
    List<Convocatoria> findByCentroIdCentro(Long idCentro);
    List<Convocatoria> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
}

