package com.eventos.pf.service.mapper;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.Integrante;
import com.eventos.pf.service.dto.EventoDTO;
import com.eventos.pf.service.dto.IntegranteDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evento} and its DTO {@link EventoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {
    @Mapping(target = "integrantes", source = "integrantes", qualifiedByName = "integranteNombreSet")
    EventoDTO toDto(Evento s);

    @Mapping(target = "removeIntegrantes", ignore = true)
    Evento toEntity(EventoDTO eventoDTO);

    @Named("integranteNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    IntegranteDTO toDtoIntegranteNombre(Integrante integrante);

    @Named("integranteNombreSet")
    default Set<IntegranteDTO> toDtoIntegranteNombreSet(Set<Integrante> integrante) {
        return integrante.stream().map(this::toDtoIntegranteNombre).collect(Collectors.toSet());
    }
}
