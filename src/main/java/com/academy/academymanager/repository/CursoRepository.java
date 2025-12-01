package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Curso entity.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByActivoTrue();
    List<Curso> findByMateriaIdMateria(Long idMateria);
    List<Curso> findByFormatoIdFormato(Long idFormato);
    boolean existsByNombre(String nombre);
}

