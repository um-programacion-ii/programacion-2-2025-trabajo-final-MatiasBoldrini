package com.eventos.pf.repository;

import com.eventos.pf.domain.SesionCompra;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SesionCompra entity.
 */
@Repository
public interface SesionCompraRepository extends JpaRepository<SesionCompra, Long> {
    @Query("select sesionCompra from SesionCompra sesionCompra where sesionCompra.usuario.login = ?#{authentication.name}")
    List<SesionCompra> findByUsuarioIsCurrentUser();

    default Optional<SesionCompra> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SesionCompra> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SesionCompra> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sesionCompra from SesionCompra sesionCompra left join fetch sesionCompra.usuario left join fetch sesionCompra.evento",
        countQuery = "select count(sesionCompra) from SesionCompra sesionCompra"
    )
    Page<SesionCompra> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sesionCompra from SesionCompra sesionCompra left join fetch sesionCompra.usuario left join fetch sesionCompra.evento")
    List<SesionCompra> findAllWithToOneRelationships();

    @Query(
        "select sesionCompra from SesionCompra sesionCompra left join fetch sesionCompra.usuario left join fetch sesionCompra.evento where sesionCompra.id =:id"
    )
    Optional<SesionCompra> findOneWithToOneRelationships(@Param("id") Long id);
}
