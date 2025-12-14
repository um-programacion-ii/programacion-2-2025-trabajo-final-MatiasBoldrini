package com.eventos.pf.service.impl;

import com.eventos.pf.domain.VentaAsiento;
import com.eventos.pf.repository.VentaAsientoRepository;
import com.eventos.pf.service.VentaAsientoService;
import com.eventos.pf.service.dto.VentaAsientoDTO;
import com.eventos.pf.service.mapper.VentaAsientoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.eventos.pf.domain.VentaAsiento}.
 */
@Service
@Transactional
public class VentaAsientoServiceImpl implements VentaAsientoService {

    private static final Logger LOG = LoggerFactory.getLogger(VentaAsientoServiceImpl.class);

    private final VentaAsientoRepository ventaAsientoRepository;

    private final VentaAsientoMapper ventaAsientoMapper;

    public VentaAsientoServiceImpl(VentaAsientoRepository ventaAsientoRepository, VentaAsientoMapper ventaAsientoMapper) {
        this.ventaAsientoRepository = ventaAsientoRepository;
        this.ventaAsientoMapper = ventaAsientoMapper;
    }

    @Override
    public VentaAsientoDTO save(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to save VentaAsiento : {}", ventaAsientoDTO);
        VentaAsiento ventaAsiento = ventaAsientoMapper.toEntity(ventaAsientoDTO);
        ventaAsiento = ventaAsientoRepository.save(ventaAsiento);
        return ventaAsientoMapper.toDto(ventaAsiento);
    }

    @Override
    public VentaAsientoDTO update(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to update VentaAsiento : {}", ventaAsientoDTO);
        VentaAsiento ventaAsiento = ventaAsientoMapper.toEntity(ventaAsientoDTO);
        ventaAsiento = ventaAsientoRepository.save(ventaAsiento);
        return ventaAsientoMapper.toDto(ventaAsiento);
    }

    @Override
    public Optional<VentaAsientoDTO> partialUpdate(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to partially update VentaAsiento : {}", ventaAsientoDTO);

        return ventaAsientoRepository
            .findById(ventaAsientoDTO.getId())
            .map(existingVentaAsiento -> {
                ventaAsientoMapper.partialUpdate(existingVentaAsiento, ventaAsientoDTO);

                return existingVentaAsiento;
            })
            .map(ventaAsientoRepository::save)
            .map(ventaAsientoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaAsientoDTO> findAll() {
        LOG.debug("Request to get all VentaAsientos");
        return ventaAsientoRepository.findAll().stream().map(ventaAsientoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaAsientoDTO> findOne(Long id) {
        LOG.debug("Request to get VentaAsiento : {}", id);
        return ventaAsientoRepository.findById(id).map(ventaAsientoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete VentaAsiento : {}", id);
        ventaAsientoRepository.deleteById(id);
    }
}
