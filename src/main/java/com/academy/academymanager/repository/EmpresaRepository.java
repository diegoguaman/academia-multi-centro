package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Empresa entity.
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByCif(String cif);
    boolean existsByCif(String cif);
}

