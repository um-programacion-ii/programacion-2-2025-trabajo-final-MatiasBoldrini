package com.eventos.pf.service.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * Sincronización de respaldo.
 *
 * Kafka es el trigger preferido, pero esto cubre caídas o retrasos.
 */
@Component
public class SyncScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    private final SyncService syncService;

    public SyncScheduler(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Cada 30 minutos.
     */
    @Scheduled(fixedRateString = "PT30M")
    public void syncProgramado() {
        try {
            LOG.debug("Ejecutando sincronización programada...");
            syncService.sincronizarEventos();
        } catch (Exception e) {
            LOG.error("Error en sync programado: {}", e.getMessage(), e);
        }
    }

    /**
     * Al iniciar la aplicación.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void syncAlIniciar() {
        try {
            LOG.info("Ejecutando sincronización inicial...");
            syncService.sincronizarEventos();
        } catch (Exception e) {
            LOG.error("Error en sync inicial: {}", e.getMessage(), e);
        }
    }
}


