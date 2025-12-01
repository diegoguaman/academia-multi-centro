package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.DatosPersonales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for DatosPersonales entity.
 */
@Repository
public interface DatosPersonalesRepository extends JpaRepository<DatosPersonales, Long> {
    Optional<DatosPersonales> findByDniNie(String dniNie);
    boolean existsByDniNie(String dniNie);
}

