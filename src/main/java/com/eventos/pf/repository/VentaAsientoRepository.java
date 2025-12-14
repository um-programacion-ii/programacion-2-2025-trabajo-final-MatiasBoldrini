package com.eventos.pf.repository;

import com.eventos.pf.domain.VentaAsiento;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VentaAsiento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VentaAsientoRepository extends JpaRepository<VentaAsiento, Long> {}
