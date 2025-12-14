package com.eventos.pf.web.rest;

import com.eventos.pf.repository.SesionCompraRepository;
import com.eventos.pf.service.SesionCompraService;
import com.eventos.pf.service.dto.SesionCompraDTO;
import com.eventos.pf.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.eventos.pf.domain.SesionCompra}.
 */
@RestController
@RequestMapping("/api/sesion-compras")
public class SesionCompraResource {

    private static final Logger LOG = LoggerFactory.getLogger(SesionCompraResource.class);

    private static final String ENTITY_NAME = "sesionCompra";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SesionCompraService sesionCompraService;

    private final SesionCompraRepository sesionCompraRepository;

    public SesionCompraResource(SesionCompraService sesionCompraService, SesionCompraRepository sesionCompraRepository) {
        this.sesionCompraService = sesionCompraService;
        this.sesionCompraRepository = sesionCompraRepository;
    }

    /**
     * {@code POST  /sesion-compras} : Create a new sesionCompra.
     *
     * @param sesionCompraDTO the sesionCompraDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sesionCompraDTO, or with status {@code 400 (Bad Request)} if the sesionCompra has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SesionCompraDTO> createSesionCompra(@Valid @RequestBody SesionCompraDTO sesionCompraDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SesionCompra : {}", sesionCompraDTO);
        if (sesionCompraDTO.getId() != null) {
            throw new BadRequestAlertException("A new sesionCompra cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sesionCompraDTO = sesionCompraService.save(sesionCompraDTO);
        return ResponseEntity.created(new URI("/api/sesion-compras/" + sesionCompraDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, sesionCompraDTO.getId().toString()))
            .body(sesionCompraDTO);
    }

    /**
     * {@code PUT  /sesion-compras/:id} : Updates an existing sesionCompra.
     *
     * @param id the id of the sesionCompraDTO to save.
     * @param sesionCompraDTO the sesionCompraDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sesionCompraDTO,
     * or with status {@code 400 (Bad Request)} if the sesionCompraDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sesionCompraDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SesionCompraDTO> updateSesionCompra(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SesionCompraDTO sesionCompraDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SesionCompra : {}, {}", id, sesionCompraDTO);
        if (sesionCompraDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sesionCompraDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sesionCompraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sesionCompraDTO = sesionCompraService.update(sesionCompraDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sesionCompraDTO.getId().toString()))
            .body(sesionCompraDTO);
    }

    /**
     * {@code PATCH  /sesion-compras/:id} : Partial updates given fields of an existing sesionCompra, field will ignore if it is null
     *
     * @param id the id of the sesionCompraDTO to save.
     * @param sesionCompraDTO the sesionCompraDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sesionCompraDTO,
     * or with status {@code 400 (Bad Request)} if the sesionCompraDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sesionCompraDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sesionCompraDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SesionCompraDTO> partialUpdateSesionCompra(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SesionCompraDTO sesionCompraDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SesionCompra partially : {}, {}", id, sesionCompraDTO);
        if (sesionCompraDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sesionCompraDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sesionCompraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SesionCompraDTO> result = sesionCompraService.partialUpdate(sesionCompraDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sesionCompraDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sesion-compras} : get all the sesionCompras.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sesionCompras in body.
     */
    @GetMapping("")
    public List<SesionCompraDTO> getAllSesionCompras(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SesionCompras");
        return sesionCompraService.findAll();
    }

    /**
     * {@code GET  /sesion-compras/:id} : get the "id" sesionCompra.
     *
     * @param id the id of the sesionCompraDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sesionCompraDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SesionCompraDTO> getSesionCompra(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SesionCompra : {}", id);
        Optional<SesionCompraDTO> sesionCompraDTO = sesionCompraService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sesionCompraDTO);
    }

    /**
     * {@code DELETE  /sesion-compras/:id} : delete the "id" sesionCompra.
     *
     * @param id the id of the sesionCompraDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSesionCompra(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SesionCompra : {}", id);
        sesionCompraService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
