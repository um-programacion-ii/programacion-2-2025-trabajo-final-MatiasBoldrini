package com.eventos.pf.web.rest;

import com.eventos.pf.security.AuthoritiesConstants;
import com.eventos.pf.service.sync.SyncService;
import com.eventos.pf.service.sync.dto.SyncResultDTO;
import com.eventos.pf.service.sync.dto.SyncStatusDTO;
import com.eventos.pf.service.sync.dto.SyncTriggerRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SyncResource {

    private static final Logger LOG = LoggerFactory.getLogger(SyncResource.class);
    private static final Duration MIN_INTERVAL = Duration.ofSeconds(10);
    private static final AtomicReference<Instant> LAST_TRIGGER = new AtomicReference<>(Instant.EPOCH);

    private final SyncService syncService;

    public SyncResource(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Endpoint llamado por el Proxy cuando Kafka notifica cambios.
     */
    @PostMapping("/trigger")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SyncResultDTO> triggerSync(@RequestBody(required = false) SyncTriggerRequest request) {
        Instant now = Instant.now();
        Instant last = LAST_TRIGGER.get();
        if (Duration.between(last, now).compareTo(MIN_INTERVAL) < 0) {
            long retryAfter = Math.max(1, MIN_INTERVAL.minus(Duration.between(last, now)).toSeconds());
            LOG.debug("Rate limit /api/sync/trigger (retryAfter={}s). request={}", retryAfter, request);
            SyncResultDTO body = new SyncResultDTO("DEBOUNCED", 0, 0, 0, 0);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).header("Retry-After", String.valueOf(retryAfter)).body(body);
        }
        LAST_TRIGGER.set(now);

        LOG.info("Sync trigger recibido: {}", request != null ? request.getSource() : "manual");
        SyncResultDTO result = syncService.sincronizarEventos();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SyncStatusDTO> getStatus() {
        return ResponseEntity.ok(syncService.getStatus());
    }
}


