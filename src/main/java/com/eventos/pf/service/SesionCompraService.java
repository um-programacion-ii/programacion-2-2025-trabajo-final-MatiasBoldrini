package com.eventos.pf.service;

import com.eventos.pf.service.dto.SesionCompraDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.eventos.pf.domain.SesionCompra}.
 */
public interface SesionCompraService {
    /**
     * Save a sesionCompra.
     *
     * @param sesionCompraDTO the entity to save.
     * @return the persisted entity.
     */
    SesionCompraDTO save(SesionCompraDTO sesionCompraDTO);

    /**
     * Updates a sesionCompra.
     *
     * @param sesionCompraDTO the entity to update.
     * @return the persisted entity.
     */
    SesionCompraDTO update(SesionCompraDTO sesionCompraDTO);

    /**
     * Partially updates a sesionCompra.
     *
     * @param sesionCompraDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SesionCompraDTO> partialUpdate(SesionCompraDTO sesionCompraDTO);

    /**
     * Get all the sesionCompras.
     *
     * @return the list of entities.
     */
    List<SesionCompraDTO> findAll();

    /**
     * Get all the sesionCompras with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SesionCompraDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" sesionCompra.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SesionCompraDTO> findOne(Long id);

    /**
     * Delete the "id" sesionCompra.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
