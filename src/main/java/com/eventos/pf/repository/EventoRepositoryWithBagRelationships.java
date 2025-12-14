package com.eventos.pf.repository;

import com.eventos.pf.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface EventoRepositoryWithBagRelationships {
    Optional<Evento> fetchBagRelationships(Optional<Evento> evento);

    List<Evento> fetchBagRelationships(List<Evento> eventos);

    Page<Evento> fetchBagRelationships(Page<Evento> eventos);
}
