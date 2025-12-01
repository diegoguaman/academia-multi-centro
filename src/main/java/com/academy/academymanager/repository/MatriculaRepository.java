package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Matricula entity.
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    Optional<Matricula> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Matricula> findByAlumnoIdUsuario(Long idAlumno);
    List<Matricula> findByConvocatoriaIdConvocatoria(Long idConvocatoria);
    List<Matricula> findByEstadoPago(Matricula.EstadoPago estadoPago);
}

