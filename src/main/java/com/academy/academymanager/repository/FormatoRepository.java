package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Formato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Formato entity.
 */
@Repository
public interface FormatoRepository extends JpaRepository<Formato, Long> {
    boolean existsByNombre(String nombre);
}

