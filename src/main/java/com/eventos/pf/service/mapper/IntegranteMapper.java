package com.eventos.pf.service.mapper;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.Integrante;
import com.eventos.pf.service.dto.EventoDTO;
import com.eventos.pf.service.dto.IntegranteDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Integrante} and its DTO {@link IntegranteDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegranteMapper extends EntityMapper<IntegranteDTO, Integrante> {
    @Mapping(target = "eventos", source = "eventos", qualifiedByName = "eventoTituloSet")
    IntegranteDTO toDto(Integrante s);

    @Mapping(target = "eventos", ignore = true)
    @Mapping(target = "removeEventos", ignore = true)
    Integrante toEntity(IntegranteDTO integranteDTO);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);

    @Named("eventoTituloSet")
    default Set<EventoDTO> toDtoEventoTituloSet(Set<Evento> evento) {
        return evento.stream().map(this::toDtoEventoTitulo).collect(Collectors.toSet());
    }
}
