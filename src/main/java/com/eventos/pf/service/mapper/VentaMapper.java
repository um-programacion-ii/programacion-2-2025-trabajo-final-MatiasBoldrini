package com.eventos.pf.service.mapper;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.User;
import com.eventos.pf.domain.Venta;
import com.eventos.pf.service.dto.EventoDTO;
import com.eventos.pf.service.dto.UserDTO;
import com.eventos.pf.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Venta} and its DTO {@link VentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaMapper extends EntityMapper<VentaDTO, Venta> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    VentaDTO toDto(Venta s);

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
