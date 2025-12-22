package com.eventos.pf.service.compra;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.SesionCompra;
import com.eventos.pf.domain.User;
import com.eventos.pf.domain.Venta;
import com.eventos.pf.domain.VentaAsiento;
import com.eventos.pf.repository.EventoRepository;
import com.eventos.pf.repository.SesionCompraRepository;
import com.eventos.pf.repository.UserRepository;
import com.eventos.pf.repository.VentaAsientoRepository;
import com.eventos.pf.repository.VentaRepository;
import com.eventos.pf.service.compra.dto.AsientoPersona;
import com.eventos.pf.service.compra.dto.AsientoSeleccion;
import com.eventos.pf.service.compra.session.CompraSesionState;
import com.eventos.pf.service.compra.session.CompraSesionStore;
import com.eventos.pf.service.catedra.dto.AsientoBloqueoDTO;
import com.eventos.pf.service.catedra.dto.AsientoVentaDTO;
import com.eventos.pf.service.catedra.dto.BloquearAsientosRequest;
import com.eventos.pf.service.catedra.dto.BloquearAsientosResponse;
import com.eventos.pf.service.catedra.dto.RealizarVentaRequest;
import com.eventos.pf.service.catedra.dto.RealizarVentaResponse;
import com.eventos.pf.service.proxy.ProxyClientService;
import com.eventos.pf.service.proxy.dto.AsientoRedisDTO;
import com.eventos.pf.service.proxy.dto.EventoAsientosDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Orquestación del flujo de compra.
 *
 * Regla de consigna: la sesión activa debe vivir en Redis local con TTL por
 * inactividad.
 * La entidad {@link SesionCompra} se mantiene en DB para tener un id
 * estable/auditoría.
 */
@Service
@Transactional
public class CompraFlowService {

    public static final int PASO_INICIADA = 1;
    public static final int PASO_ASIENTOS_SELECCIONADOS = 2;
    public static final int PASO_ASIENTOS_BLOQUEADOS = 3;
    public static final int PASO_NOMBRES_CARGADOS = 4;
    public static final int PASO_CONFIRMADA = 5;

    private final SesionCompraRepository sesionCompraRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final VentaRepository ventaRepository;
    private final VentaAsientoRepository ventaAsientoRepository;
    private final ProxyClientService proxyClientService;
    private final CompraSesionStore compraSesionStore;
    private final ObjectMapper objectMapper;

    public CompraFlowService(
            SesionCompraRepository sesionCompraRepository,
            EventoRepository eventoRepository,
            UserRepository userRepository,
            VentaRepository ventaRepository,
            VentaAsientoRepository ventaAsientoRepository,
            ProxyClientService proxyClientService,
            CompraSesionStore compraSesionStore,
            ObjectMapper objectMapper) {
        this.sesionCompraRepository = sesionCompraRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
        this.ventaRepository = ventaRepository;
        this.ventaAsientoRepository = ventaAsientoRepository;
        this.proxyClientService = proxyClientService;
        this.compraSesionStore = compraSesionStore;
        this.objectMapper = objectMapper;
    }

    public CompraSesionState iniciar(Long eventoIdLocal, String userLogin) {
        if (eventoIdLocal == null || eventoIdLocal <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventoIdLocal inválido");
        }
        String login = requireLogin(userLogin);

        Evento evento = eventoRepository.findById(eventoIdLocal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (evento.getEventoIdCatedra() == null || evento.getEventoIdCatedra() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento sin eventoIdCatedra");
        }

        // Redis es la fuente de verdad: revisar primero si hay sesión en Redis
        Optional<CompraSesionState> redisState = compraSesionStore.get(login);
        if (redisState.isPresent()) {
            CompraSesionState state = redisState.get();
            if (state.getEventoIdLocal() != null && state.getEventoIdLocal().equals(eventoIdLocal)) {
                // Retomar misma sesión (mismo evento) y tocar TTL
                compraSesionStore.save(login, state);
                return state;
            }
            // Evento distinto: cerrar sesión actual (DB+Redis) y crear nueva
            if (state.getSesionId() != null) {
                sesionCompraRepository.findById(state.getSesionId()).ifPresent(s -> cerrarSesionDbYRedis(login, s));
            } else {
                compraSesionStore.delete(login);
            }
        } else {
            // No hay sesión en Redis: cerrar cualquier sesión activa huérfana en DB
            cerrarSesionesHuerfanasDb(login);
        }

        // Crear nueva sesión
        User user = userRepository.findOneByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        SesionCompra nueva = new SesionCompra()
                .activa(true)
                .pasoActual(PASO_INICIADA)
                .fechaCreacion(Instant.now())
                .fechaExpiracion(null)
                .usuario(user)
                .evento(evento);
        nueva = sesionCompraRepository.save(nueva);

        CompraSesionState state = new CompraSesionState();
        state.setSesionId(nueva.getId());
        state.setEventoIdLocal(eventoIdLocal);
        state.setEventoIdCatedra(evento.getEventoIdCatedra());
        state.setPasoActual(PASO_INICIADA);
        state.setAsientosSeleccionadosJson(null);
        state.setDatosPersonasJson(null);
        compraSesionStore.save(login, state);
        return state;
    }

    public CompraSesionState seleccionarAsientos(Long sesionId, List<AsientoSeleccion> asientos, String userLogin,
            String jwt) {
        String login = requireLogin(userLogin);
        if (sesionId == null || sesionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT requerido");
        }
        if (asientos == null || asientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar al menos 1 asiento");
        }
        if (asientos.size() > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo 4 asientos por sesión");
        }

        CompraSesionState state = requireSesionActivaRedis(login, sesionId);
        SesionCompra sesionDb = requireSesionCompraDbOwned(sesionId, login);

        // Permitir seleccionar desde pasos 1..4 (retroceso desde nombres cargados)
        int pasoActual = sesionDb.getPasoActual() == null ? PASO_INICIADA : sesionDb.getPasoActual();
        if (pasoActual < PASO_INICIADA || pasoActual > PASO_NOMBRES_CARGADOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede seleccionar asientos desde el paso actual");
        }

        Evento evento = sesionDb.getEvento();
        if (evento == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sesión sin evento asociado");
        }
        validarCoordenadasEnEvento(asientos, evento);
        validarSinDuplicados(asientos);

        EventoAsientosDTO ocupados = proxyClientService.obtenerAsientos(requireEventoIdCatedra(evento), jwt);
        for (AsientoSeleccion a : asientos) {
            if (ocupados.estaOcupado(a.getFila(), a.getColumna())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Asiento no disponible (fila=" + a.getFila() + ", columna=" + a.getColumna() + ")");
            }
        }

        String json = writeJson(asientos);
        sesionDb.setAsientosSeleccionados(json);
        sesionDb.setPasoActual(PASO_ASIENTOS_SELECCIONADOS);
        // Limpiar nombres al cambiar selección (volver atrás)
        sesionDb.setDatosPersonas(null);
        sesionCompraRepository.save(sesionDb);

        state.setPasoActual(PASO_ASIENTOS_SELECCIONADOS);
        state.setAsientosSeleccionadosJson(json);
        state.setDatosPersonasJson(null);
        compraSesionStore.save(login, state);
        return state;
    }

    public BloquearAsientosResponse bloquear(Long sesionId, String userLogin, String jwt) {
        String login = requireLogin(userLogin);
        if (sesionId == null || sesionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT requerido");
        }

        CompraSesionState state = requireSesionActivaRedis(login, sesionId);
        SesionCompra sesionDb = requireSesionCompraDbOwned(sesionId, login);

        // Permitir bloquear desde pasos 2..4 (asientos seleccionados, bloqueados o
        // nombres cargados)
        int pasoActual = sesionDb.getPasoActual() == null ? PASO_INICIADA : sesionDb.getPasoActual();
        if (pasoActual < PASO_ASIENTOS_SELECCIONADOS || pasoActual > PASO_NOMBRES_CARGADOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe seleccionar asientos antes de bloquear");
        }

        List<AsientoSeleccion> seleccionados = readAsientosSeleccionados(sesionDb.getAsientosSeleccionados());
        if (seleccionados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay asientos seleccionados");
        }

        Evento evento = sesionDb.getEvento();
        Long eventoIdCatedra = requireEventoIdCatedra(evento);

        BloquearAsientosRequest req = new BloquearAsientosRequest();
        req.setEventoId(eventoIdCatedra);
        req.setAsientos(seleccionados.stream().map(a -> new AsientoBloqueoDTO(a.getFila(), a.getColumna())).toList());

        BloquearAsientosResponse resp = proxyClientService.bloquearAsientos(req, jwt);
        if (resp == null || resp.getResultado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta inválida del proxy al bloquear");
        }
        if (!resp.getResultado()) {
            // No avanzar el paso si el bloqueo falla
            return resp;
        }

        sesionDb.setPasoActual(PASO_ASIENTOS_BLOQUEADOS);
        sesionCompraRepository.save(sesionDb);

        state.setPasoActual(PASO_ASIENTOS_BLOQUEADOS);
        compraSesionStore.save(login, state);
        return resp;
    }

    public CompraSesionState cargarNombres(Long sesionId, List<AsientoPersona> asientosPersonas, String userLogin) {
        String login = requireLogin(userLogin);
        if (sesionId == null || sesionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId inválido");
        }
        if (asientosPersonas == null || asientosPersonas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe cargar nombres para los asientos");
        }

        CompraSesionState state = requireSesionActivaRedis(login, sesionId);
        SesionCompra sesionDb = requireSesionCompraDbOwned(sesionId, login);

        // Permitir cargar nombres desde pasos 3..4 (bloqueados o nombres ya cargados)
        int pasoActual = sesionDb.getPasoActual() == null ? PASO_INICIADA : sesionDb.getPasoActual();
        if (pasoActual < PASO_ASIENTOS_BLOQUEADOS || pasoActual > PASO_NOMBRES_CARGADOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe bloquear asientos antes de cargar nombres");
        }

        List<AsientoSeleccion> seleccionados = readAsientosSeleccionados(sesionDb.getAsientosSeleccionados());
        if (seleccionados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay asientos seleccionados");
        }
        if (asientosPersonas.size() != seleccionados.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad de personas no coincide con la selección");
        }

        validarPersonasCoincidenConSeleccion(seleccionados, asientosPersonas);

        String json = writeJson(asientosPersonas);
        sesionDb.setDatosPersonas(json);
        sesionDb.setPasoActual(PASO_NOMBRES_CARGADOS);
        sesionCompraRepository.save(sesionDb);

        state.setPasoActual(PASO_NOMBRES_CARGADOS);
        state.setDatosPersonasJson(json);
        compraSesionStore.save(login, state);
        return state;
    }

    public ConfirmarCompraResult confirmar(Long sesionId, String userLogin, String jwt) {
        String login = requireLogin(userLogin);
        if (sesionId == null || sesionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT requerido");
        }

        CompraSesionState state = requireSesionActivaRedis(login, sesionId);
        SesionCompra sesionDb = requireSesionCompraDbOwned(sesionId, login);
        if (sesionDb.getPasoActual() == null || sesionDb.getPasoActual() < PASO_NOMBRES_CARGADOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe cargar nombres antes de confirmar");
        }

        Evento evento = sesionDb.getEvento();
        Long eventoIdCatedra = requireEventoIdCatedra(evento);

        List<AsientoPersona> asientosPersonas = readAsientosPersonas(sesionDb.getDatosPersonas());
        if (asientosPersonas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay nombres cargados");
        }
        if (asientosPersonas.size() > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo 4 asientos por sesión");
        }

        // Validar bloqueo vigente (consigna: venta sólo con bloqueo activo/no expirado)
        EventoAsientosDTO redisAsientos = proxyClientService.obtenerAsientos(eventoIdCatedra, jwt);
        for (AsientoPersona a : asientosPersonas) {
            if (!estaBloqueadoVigente(redisAsientos, a.getFila(), a.getColumna())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No hay bloqueo activo para el asiento (fila=" + a.getFila() + ", columna=" + a.getColumna()
                                + ")");
            }
        }

        BigDecimal precioUnitario = evento.getPrecioEntrada() == null ? BigDecimal.ZERO : evento.getPrecioEntrada();
        BigDecimal precioVenta = precioUnitario.multiply(BigDecimal.valueOf(asientosPersonas.size()));

        RealizarVentaRequest req = new RealizarVentaRequest();
        req.setEventoId(eventoIdCatedra);
        req.setFecha(Instant.now());
        req.setPrecioVenta(precioVenta);
        req.setAsientos(asientosPersonas.stream()
                .map(a -> new AsientoVentaDTO(a.getFila(), a.getColumna(), a.getPersona())).toList());

        RealizarVentaResponse resp = proxyClientService.realizarVenta(req, jwt);
        if (resp == null || resp.getResultado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta inválida del proxy al vender");
        }

        Venta venta = new Venta();
        venta.setEvento(evento);
        venta.setUsuario(userRepository.findOneByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
        venta.setFechaVenta(resp.getFechaVenta() == null ? Instant.now() : resp.getFechaVenta());
        venta.setPrecioVenta(resp.getPrecioVenta() == null ? precioVenta : resp.getPrecioVenta());
        venta.setResultado(resp.getResultado());
        venta.setDescripcion(resp.getDescripcion());
        venta.setVentaIdCatedra(resp.getVentaId());
        venta = ventaRepository.save(venta);

        if (resp.getAsientos() != null) {
            for (var a : resp.getAsientos()) {
                VentaAsiento va = new VentaAsiento();
                va.setVenta(venta);
                va.setFila(a.getFila());
                va.setColumna(a.getColumna());
                va.setPersona(a.getPersona());
                va.setEstado(a.getEstado());
                ventaAsientoRepository.save(va);
            }
        }

        // Si fue exitosa, cerrar sesión; si falla, mantenerla para reintentar (sin
        // borrar Redis)
        if (Boolean.TRUE.equals(resp.getResultado())) {
            sesionDb.setPasoActual(PASO_CONFIRMADA);
            sesionDb.setActiva(false);
            sesionDb.setFechaExpiracion(Instant.now());
            sesionCompraRepository.save(sesionDb);
            compraSesionStore.delete(login);
        } else {
            // refrescar TTL igual
            state.setPasoActual(PASO_NOMBRES_CARGADOS);
            compraSesionStore.save(login, state);
        }

        return new ConfirmarCompraResult(venta.getId(), resp);
    }

    @Transactional(readOnly = true)
    public Optional<CompraSesionState> getSesionActual(String userLogin) {
        String login = requireLogin(userLogin);
        Optional<CompraSesionState> redisState = compraSesionStore.get(login);
        if (redisState.isEmpty()) {
            // Redis no tiene sesión: cerrar cualquier sesión activa huérfana en DB
            cerrarSesionesHuerfanasDb(login);
        }
        return redisState;
    }

    public void cerrarSesionActual(String userLogin) {
        String login = requireLogin(userLogin);
        compraSesionStore.get(login).ifPresent(state -> {
            if (state.getSesionId() != null) {
                sesionCompraRepository.findById(state.getSesionId()).ifPresent(s -> cerrarSesionDbYRedis(login, s));
            } else {
                compraSesionStore.delete(login);
            }
        });
    }

    private void cerrarSesionDbYRedis(String login, SesionCompra s) {
        s.setActiva(false);
        s.setFechaExpiracion(Instant.now());
        sesionCompraRepository.save(s);
        compraSesionStore.delete(login);
    }

    /**
     * Cierra todas las sesiones activas en DB para el usuario dado.
     * Se llama cuando Redis no tiene sesión activa, para que DB refleje la realidad
     * (TTL expirado).
     */
    private void cerrarSesionesHuerfanasDb(String login) {
        List<SesionCompra> activas = sesionCompraRepository.findByUsuarioLoginAndActivaIsTrue(login);
        if (!activas.isEmpty()) {
            Instant ahora = Instant.now();
            for (SesionCompra s : activas) {
                s.setActiva(false);
                s.setFechaExpiracion(ahora);
            }
            sesionCompraRepository.saveAll(activas);
        }
    }

    private CompraSesionState requireSesionActivaRedis(String login, Long sesionId) {
        CompraSesionState state = compraSesionStore.get(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay sesión activa"));
        if (state.getSesionId() == null || !state.getSesionId().equals(sesionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sesión activa no coincide con sesionId");
        }
        return state;
    }

    private SesionCompra requireSesionCompraDbOwned(Long sesionId, String login) {
        SesionCompra s = sesionCompraRepository.findById(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (s.getUsuario() == null || s.getUsuario().getLogin() == null || !s.getUsuario().getLogin().equals(login)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La sesión no pertenece al usuario");
        }
        if (!Boolean.TRUE.equals(s.getActiva())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sesión no está activa");
        }
        return s;
    }

    private void validarCoordenadasEnEvento(List<AsientoSeleccion> asientos, Evento evento) {
        Integer filas = evento.getFilaAsientos();
        Integer cols = evento.getColumnaAsientos();
        if (filas == null || cols == null || filas <= 0 || cols <= 0) {
            // Si no tenemos dimensión, no podemos validar límites.
            return;
        }
        for (AsientoSeleccion a : asientos) {
            if (a == null || a.getFila() == null || a.getColumna() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asiento inválido");
            }
            if (a.getFila() <= 0 || a.getColumna() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fila/columna deben ser positivas");
            }
            if (a.getFila() > filas || a.getColumna() > cols) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fila/columna fuera de rango del evento");
            }
        }
    }

    private void validarSinDuplicados(List<AsientoSeleccion> asientos) {
        Set<String> seen = new HashSet<>();
        for (AsientoSeleccion a : asientos) {
            String k = a.getFila() + ":" + a.getColumna();
            if (!seen.add(k)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se permiten asientos duplicados");
            }
        }
    }

    private void validarPersonasCoincidenConSeleccion(List<AsientoSeleccion> seleccionados,
            List<AsientoPersona> personas) {
        Set<String> sel = new HashSet<>(seleccionados.stream().map(a -> a.getFila() + ":" + a.getColumna()).toList());
        Set<String> seen = new HashSet<>();
        for (AsientoPersona p : personas) {
            if (p == null || p.getFila() == null || p.getColumna() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AsientoPersona inválido");
            }
            String k = p.getFila() + ":" + p.getColumna();
            if (!sel.contains(k)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AsientoPersona no pertenece a la selección");
            }
            if (!seen.add(k)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AsientoPersona duplicado");
            }
        }
    }

    private boolean estaBloqueadoVigente(EventoAsientosDTO dto, int fila, int columna) {
        if (dto == null || dto.getAsientos() == null || dto.getAsientos().isEmpty()) {
            return false;
        }
        Instant ahora = Instant.now();
        for (AsientoRedisDTO a : dto.getAsientos()) {
            if (a == null || a.getFila() == null || a.getColumna() == null) {
                continue;
            }
            if (a.getFila() == fila && a.getColumna() == columna) {
                String estado = a.getEstado() == null ? "" : a.getEstado().trim().toLowerCase();
                if (estado.equals("vendido") || estado.equals("ocupado")) {
                    return false;
                }
                boolean esBloqueo = estado.equals("bloqueado") || estado.startsWith("bloqueo");
                if (!esBloqueo) {
                    return false;
                }
                if (a.getExpira() == null) {
                    return true; // conservador
                }
                return a.getExpira().isAfter(ahora);
            }
        }
        return false;
    }

    private Long requireEventoIdCatedra(Evento evento) {
        if (evento == null || evento.getEventoIdCatedra() == null || evento.getEventoIdCatedra() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento sin eventoIdCatedra");
        }
        return evento.getEventoIdCatedra();
    }

    private List<AsientoSeleccion> readAsientosSeleccionados(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<AsientoSeleccion>>() {
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON inválido de asientosSeleccionados", e);
        }
    }

    private List<AsientoPersona> readAsientosPersonas(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<AsientoPersona>>() {
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON inválido de datosPersonas", e);
        }
    }

    private String writeJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializando JSON", e);
        }
    }

    private String requireLogin(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return userLogin;
    }

    public record ConfirmarCompraResult(Long ventaIdLocal, RealizarVentaResponse ventaCatedra) {
    }
}
