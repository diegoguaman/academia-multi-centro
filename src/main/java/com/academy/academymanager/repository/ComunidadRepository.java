package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Comunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Comunidad entity.
 */
@Repository
public interface ComunidadRepository extends JpaRepository<Comunidad, Long> {
    Optional<Comunidad> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}

