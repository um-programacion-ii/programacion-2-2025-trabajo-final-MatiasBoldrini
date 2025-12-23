package com.eventos.pf.service.sync.dto;

import java.time.Instant;

public class SyncStatusDTO {
    private Instant ultimaSync;
    private String ultimoResultado;

    public SyncStatusDTO() {}

    public SyncStatusDTO(Instant ultimaSync, String ultimoResultado) {
        this.ultimaSync = ultimaSync;
        this.ultimoResultado = ultimoResultado;
    }

    public Instant getUltimaSync() {
        return ultimaSync;
    }

    public void setUltimaSync(Instant ultimaSync) {
        this.ultimaSync = ultimaSync;
    }

    public String getUltimoResultado() {
        return ultimoResultado;
    }

    public void setUltimoResultado(String ultimoResultado) {
        this.ultimoResultado = ultimoResultado;
    }
}


