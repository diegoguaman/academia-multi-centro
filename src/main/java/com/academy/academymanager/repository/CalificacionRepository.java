package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Calificacion entity.
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    Optional<Calificacion> findByMatriculaIdMatricula(Long idMatricula);
    List<Calificacion> findByMatriculaIdMatriculaOrderByFechaCreacionDesc(Long idMatricula);
}

