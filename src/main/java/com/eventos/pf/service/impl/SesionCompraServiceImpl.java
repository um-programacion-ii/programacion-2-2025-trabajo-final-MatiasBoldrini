package com.eventos.pf.service.impl;

import com.eventos.pf.domain.SesionCompra;
import com.eventos.pf.repository.SesionCompraRepository;
import com.eventos.pf.service.SesionCompraService;
import com.eventos.pf.service.dto.SesionCompraDTO;
import com.eventos.pf.service.mapper.SesionCompraMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.eventos.pf.domain.SesionCompra}.
 */
@Service
@Transactional
public class SesionCompraServiceImpl implements SesionCompraService {

    private static final Logger LOG = LoggerFactory.getLogger(SesionCompraServiceImpl.class);

    private final SesionCompraRepository sesionCompraRepository;

    private final SesionCompraMapper sesionCompraMapper;

    public SesionCompraServiceImpl(SesionCompraRepository sesionCompraRepository, SesionCompraMapper sesionCompraMapper) {
        this.sesionCompraRepository = sesionCompraRepository;
        this.sesionCompraMapper = sesionCompraMapper;
    }

    @Override
    public SesionCompraDTO save(SesionCompraDTO sesionCompraDTO) {
        LOG.debug("Request to save SesionCompra : {}", sesionCompraDTO);
        SesionCompra sesionCompra = sesionCompraMapper.toEntity(sesionCompraDTO);
        sesionCompra = sesionCompraRepository.save(sesionCompra);
        return sesionCompraMapper.toDto(sesionCompra);
    }

    @Override
    public SesionCompraDTO update(SesionCompraDTO sesionCompraDTO) {
        LOG.debug("Request to update SesionCompra : {}", sesionCompraDTO);
        SesionCompra sesionCompra = sesionCompraMapper.toEntity(sesionCompraDTO);
        sesionCompra = sesionCompraRepository.save(sesionCompra);
        return sesionCompraMapper.toDto(sesionCompra);
    }

    @Override
    public Optional<SesionCompraDTO> partialUpdate(SesionCompraDTO sesionCompraDTO) {
        LOG.debug("Request to partially update SesionCompra : {}", sesionCompraDTO);

        return sesionCompraRepository
            .findById(sesionCompraDTO.getId())
            .map(existingSesionCompra -> {
                sesionCompraMapper.partialUpdate(existingSesionCompra, sesionCompraDTO);

                return existingSesionCompra;
            })
            .map(sesionCompraRepository::save)
            .map(sesionCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionCompraDTO> findAll() {
        LOG.debug("Request to get all SesionCompras");
        return sesionCompraRepository.findAll().stream().map(sesionCompraMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<SesionCompraDTO> findAllWithEagerRelationships(Pageable pageable) {
        return sesionCompraRepository.findAllWithEagerRelationships(pageable).map(sesionCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SesionCompraDTO> findOne(Long id) {
        LOG.debug("Request to get SesionCompra : {}", id);
        return sesionCompraRepository.findOneWithEagerRelationships(id).map(sesionCompraMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SesionCompra : {}", id);
        sesionCompraRepository.deleteById(id);
    }
}
