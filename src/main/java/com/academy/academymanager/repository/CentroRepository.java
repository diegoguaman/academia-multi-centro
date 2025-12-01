package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Centro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Centro entity.
 */
@Repository
public interface CentroRepository extends JpaRepository<Centro, Long> {
    Optional<Centro> findByCodigoCentro(String codigoCentro);
    boolean existsByCodigoCentro(String codigoCentro);
    List<Centro> findByEmpresaIdEmpresa(Long idEmpresa);
    List<Centro> findByComunidadIdComunidad(Long idComunidad);
    List<Centro> findByActivoTrue();
}

