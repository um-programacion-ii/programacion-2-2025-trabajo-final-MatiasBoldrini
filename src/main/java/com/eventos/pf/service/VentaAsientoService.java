package com.eventos.pf.service;

import com.eventos.pf.service.dto.VentaAsientoDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.eventos.pf.domain.VentaAsiento}.
 */
public interface VentaAsientoService {
    /**
     * Save a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to save.
     * @return the persisted entity.
     */
    VentaAsientoDTO save(VentaAsientoDTO ventaAsientoDTO);

    /**
     * Updates a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to update.
     * @return the persisted entity.
     */
    VentaAsientoDTO update(VentaAsientoDTO ventaAsientoDTO);

    /**
     * Partially updates a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VentaAsientoDTO> partialUpdate(VentaAsientoDTO ventaAsientoDTO);

    /**
     * Get all the ventaAsientos.
     *
     * @return the list of entities.
     */
    List<VentaAsientoDTO> findAll();

    /**
     * Get the "id" ventaAsiento.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VentaAsientoDTO> findOne(Long id);

    /**
     * Delete the "id" ventaAsiento.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
