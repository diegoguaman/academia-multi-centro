package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Factura entity.
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);
}

