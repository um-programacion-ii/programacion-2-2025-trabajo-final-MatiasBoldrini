package com.eventos.pf.service.compra.session;

import java.util.Optional;

/**
 * Store de sesión activa de compra (Redis local).
 */
public interface CompraSesionStore {
    Optional<CompraSesionState> get(String userLogin);

    void save(String userLogin, CompraSesionState state);

    void delete(String userLogin);
}





