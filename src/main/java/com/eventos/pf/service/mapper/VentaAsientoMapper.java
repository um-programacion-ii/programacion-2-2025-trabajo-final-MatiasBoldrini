package com.eventos.pf.service.mapper;

import com.eventos.pf.domain.Venta;
import com.eventos.pf.domain.VentaAsiento;
import com.eventos.pf.service.dto.VentaAsientoDTO;
import com.eventos.pf.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VentaAsiento} and its DTO {@link VentaAsientoDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaAsientoMapper extends EntityMapper<VentaAsientoDTO, VentaAsiento> {
    @Mapping(target = "venta", source = "venta", qualifiedByName = "ventaId")
    VentaAsientoDTO toDto(VentaAsiento s);

    @Named("ventaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VentaDTO toDtoVentaId(Venta venta);
}
