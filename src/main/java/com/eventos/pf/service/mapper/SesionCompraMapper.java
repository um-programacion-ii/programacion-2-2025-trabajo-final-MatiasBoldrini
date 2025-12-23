package com.eventos.pf.service.mapper;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.SesionCompra;
import com.eventos.pf.domain.User;
import com.eventos.pf.service.dto.EventoDTO;
import com.eventos.pf.service.dto.SesionCompraDTO;
import com.eventos.pf.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SesionCompra} and its DTO {@link SesionCompraDTO}.
 */
@Mapper(componentModel = "spring")
public interface SesionCompraMapper extends EntityMapper<SesionCompraDTO, SesionCompra> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    SesionCompraDTO toDto(SesionCompra s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);
}
