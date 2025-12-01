package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.EntidadSubvencionadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EntidadSubvencionadora entity.
 */
@Repository
public interface EntidadSubvencionadoraRepository extends JpaRepository<EntidadSubvencionadora, Long> {
    Optional<EntidadSubvencionadora> findByCodigoOficial(String codigoOficial);
    boolean existsByCodigoOficial(String codigoOficial);
}

