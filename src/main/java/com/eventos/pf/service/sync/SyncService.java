package com.eventos.pf.service.sync;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.Integrante;
import com.eventos.pf.domain.enumeration.EventoEstado;
import com.eventos.pf.repository.EventoRepository;
import com.eventos.pf.repository.IntegranteRepository;
import com.eventos.pf.service.catedra.CatedraClientService;
import com.eventos.pf.service.catedra.dto.EventoCatedraDTO;
import com.eventos.pf.service.catedra.dto.IntegranteCatedraDTO;
import com.eventos.pf.service.sync.dto.SyncResultDTO;
import com.eventos.pf.service.sync.dto.SyncStatusDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SyncService {

    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final CatedraClientService catedraClientService;
    private final EventoRepository eventoRepository;
    private final IntegranteRepository integranteRepository;

    private volatile Instant ultimaSync;
    private volatile String ultimoResultado = "NUNCA_SINCRONIZADO";

    public SyncService(
        CatedraClientService catedraClientService,
        EventoRepository eventoRepository,
        IntegranteRepository integranteRepository
    ) {
        this.catedraClientService = catedraClientService;
        this.eventoRepository = eventoRepository;
        this.integranteRepository = integranteRepository;
    }

    public SyncResultDTO sincronizarEventos() {
        Instant inicio = Instant.now();
        int creados = 0;
        int actualizados = 0;
        int cancelados = 0;

        try {
            List<EventoCatedraDTO> eventosCatedra = catedraClientService.getEventos();
            Set<Long> idsCatedra = eventosCatedra.stream().map(EventoCatedraDTO::getId).filter(Objects::nonNull).collect(Collectors.toSet());

            for (EventoCatedraDTO dto : eventosCatedra) {
                if (dto.getId() == null) {
                    continue;
                }

                Optional<Evento> existente = eventoRepository.findByEventoIdCatedra(dto.getId());
                if (existente.isPresent()) {
                    Evento evento = existente.get();
                    boolean cambios = aplicarDatosSiCambio(evento, dto);
                    if (cambios) {
                        evento.setUltimaActualizacion(Instant.now());
                        eventoRepository.save(evento);
                        actualizados++;
                    }
                } else {
                    Evento nuevo = new Evento();
                    nuevo.setEventoIdCatedra(dto.getId());
                    nuevo.setEstado(EventoEstado.ACTIVO);
                    aplicarDatos(nuevo, dto);
                    nuevo.setUltimaActualizacion(Instant.now());
                    eventoRepository.save(nuevo);
                    creados++;
                }
            }

            // Marcar CANCELADO a eventos que ya no están en cátedra
            List<Evento> locales = eventoRepository.findByEstadoNot(EventoEstado.CANCELADO);
            Instant now = Instant.now();
            for (Evento local : locales) {
                Long idCatedra = local.getEventoIdCatedra();
                if (idCatedra != null && !idsCatedra.contains(idCatedra)) {
                    local.setEstado(EventoEstado.CANCELADO);
                    local.setUltimaActualizacion(now);
                    eventoRepository.save(local);
                    cancelados++;
                }
            }

            ultimaSync = Instant.now();
            ultimoResultado = "OK";
            long durationMs = Duration.between(inicio, Instant.now()).toMillis();
            LOG.info("Sync OK: {} creados, {} actualizados, {} cancelados ({}ms)", creados, actualizados, cancelados, durationMs);
            return new SyncResultDTO("OK", creados, actualizados, cancelados, durationMs);
        } catch (Exception e) {
            ultimoResultado = "ERROR: " + e.getMessage();
            LOG.error("Error en sincronización: {}", e.getMessage(), e);
            throw e;
        }
    }

    public SyncStatusDTO getStatus() {
        return new SyncStatusDTO(ultimaSync, ultimoResultado);
    }

    private boolean aplicarDatosSiCambio(Evento evento, EventoCatedraDTO dto) {
        Snapshot before = Snapshot.from(evento);
        aplicarDatos(evento, dto);
        Snapshot after = Snapshot.from(evento);
        return !before.equals(after);
    }

    private void aplicarDatos(Evento evento, EventoCatedraDTO dto) {
        evento.setTitulo(dto.getTitulo());
        evento.setResumen(dto.getResumen());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setDireccion(dto.getDireccion());
        evento.setImagen(dto.getImagen());
        evento.setFilaAsientos(dto.getFilaAsientos());
        evento.setColumnaAsientos(dto.getColumnAsientos());
        evento.setPrecioEntrada(dto.getPrecioEntrada());
        evento.setEventoTipo(dto.getEventoTipo() != null ? dto.getEventoTipo().getNombre() : null);

        if (dto.getIntegrantes() != null) {
            Set<Integrante> integrantes = new HashSet<>();
            for (IntegranteCatedraDTO i : dto.getIntegrantes()) {
                if (i == null) continue;
                String nombre = emptyToNull(i.getNombre());
                String apellido = emptyToNull(i.getApellido());
                String identificacion = emptyToNull(i.getIdentificacion());
                if (nombre == null || apellido == null) continue;

                Integrante integrante = integranteRepository
                    .findOneByNombreAndApellidoAndIdentificacion(nombre, apellido, identificacion)
                    .orElseGet(() -> {
                        Integrante nuevo = new Integrante();
                        nuevo.setNombre(nombre);
                        nuevo.setApellido(apellido);
                        nuevo.setIdentificacion(identificacion);
                        return integranteRepository.save(nuevo);
                    });
                integrantes.add(integrante);
            }
            evento.setIntegrantes(integrantes);
        }
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static final class Snapshot {
        private final String titulo;
        private final String resumen;
        private final String descripcion;
        private final Instant fecha;
        private final String direccion;
        private final String imagen;
        private final Integer fila;
        private final Integer columna;
        private final String tipo;
        private final Set<Long> integrantesIds;
        private final EventoEstado estado;

        private Snapshot(
            String titulo,
            String resumen,
            String descripcion,
            Instant fecha,
            String direccion,
            String imagen,
            Integer fila,
            Integer columna,
            String tipo,
            Set<Long> integrantesIds,
            EventoEstado estado
        ) {
            this.titulo = titulo;
            this.resumen = resumen;
            this.descripcion = descripcion;
            this.fecha = fecha;
            this.direccion = direccion;
            this.imagen = imagen;
            this.fila = fila;
            this.columna = columna;
            this.tipo = tipo;
            this.integrantesIds = integrantesIds;
            this.estado = estado;
        }

        static Snapshot from(Evento e) {
            Set<Long> ids = e.getIntegrantes() == null
                ? Set.of()
                : e.getIntegrantes().stream().map(Integrante::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            return new Snapshot(
                e.getTitulo(),
                e.getResumen(),
                e.getDescripcion(),
                e.getFecha(),
                e.getDireccion(),
                e.getImagen(),
                e.getFilaAsientos(),
                e.getColumnaAsientos(),
                e.getEventoTipo(),
                ids,
                e.getEstado()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Snapshot other)) return false;
            return (
                Objects.equals(titulo, other.titulo) &&
                Objects.equals(resumen, other.resumen) &&
                Objects.equals(descripcion, other.descripcion) &&
                Objects.equals(fecha, other.fecha) &&
                Objects.equals(direccion, other.direccion) &&
                Objects.equals(imagen, other.imagen) &&
                Objects.equals(fila, other.fila) &&
                Objects.equals(columna, other.columna) &&
                Objects.equals(tipo, other.tipo) &&
                Objects.equals(integrantesIds, other.integrantesIds) &&
                Objects.equals(estado, other.estado)
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(titulo, resumen, descripcion, fecha, direccion, imagen, fila, columna, tipo, integrantesIds, estado);
        }
    }
}


