package com.eventos.pf.repository;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.enumeration.EventoEstado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Evento entity.
 *
 * When extending this class, extend EventoRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface EventoRepository
    extends EventoRepositoryWithBagRelationships, JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {
    Optional<Evento> findByEventoIdCatedra(Long eventoIdCatedra);

    List<Evento> findByEstadoNot(EventoEstado estado);

    default Optional<Evento> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Evento> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Evento> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
