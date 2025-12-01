package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Materia entity.
 */
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findByActivoTrue();
    boolean existsByNombre(String nombre);
}

