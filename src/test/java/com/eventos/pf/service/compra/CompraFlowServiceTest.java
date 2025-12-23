package com.eventos.pf.service.compra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.SesionCompra;
import com.eventos.pf.domain.User;
import com.eventos.pf.repository.EventoRepository;
import com.eventos.pf.repository.SesionCompraRepository;
import com.eventos.pf.repository.UserRepository;
import com.eventos.pf.repository.VentaAsientoRepository;
import com.eventos.pf.repository.VentaRepository;
import com.eventos.pf.service.compra.dto.AsientoPersona;
import com.eventos.pf.service.compra.dto.AsientoSeleccion;
import com.eventos.pf.service.compra.session.CompraSesionState;
import com.eventos.pf.service.compra.session.CompraSesionStore;
import com.eventos.pf.service.proxy.ProxyClientService;
import com.eventos.pf.service.proxy.dto.EventoAsientosDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

/**
 * Tests unitarios para {@link CompraFlowService}.
 */
@ExtendWith(MockitoExtension.class)
class CompraFlowServiceTest {

    @Mock
    private SesionCompraRepository sesionCompraRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private VentaAsientoRepository ventaAsientoRepository;

    @Mock
    private ProxyClientService proxyClientService;

    @Mock
    private CompraSesionStore compraSesionStore;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CompraFlowService service;

    private User mockUser;
    private Evento mockEvento;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testuser");

        mockEvento = new Evento();
        mockEvento.setId(1L);
        mockEvento.setEventoIdCatedra(100L);
        mockEvento.setFilaAsientos(10);
        mockEvento.setColumnaAsientos(10);
    }

    /**
     * Test 1: No resucitar sesión desde DB cuando Redis expiró.
     * Si Redis no tiene sesión, cualquier sesión activa en DB debe cerrarse.
     */
    @Test
    @SuppressWarnings("unchecked")
    void iniciar_whenRedisEmpty_shouldCloseOrphanDbSession() {
        // Given: sesión activa en DB pero no en Redis
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(mockEvento));
        when(userRepository.findOneByLogin("testuser")).thenReturn(Optional.of(mockUser));
        when(compraSesionStore.get("testuser")).thenReturn(Optional.empty());

        SesionCompra sesionHuerfana = new SesionCompra();
        sesionHuerfana.setId(10L);
        sesionHuerfana.setActiva(true);
        sesionHuerfana.setUsuario(mockUser);
        sesionHuerfana.setEvento(mockEvento);

        when(sesionCompraRepository.findByUsuarioLoginAndActivaIsTrue("testuser")).thenReturn(List.of(sesionHuerfana));
        when(sesionCompraRepository.save(any(SesionCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CompraSesionState result = service.iniciar(1L, "testuser");

        // Then: nueva sesión creada
        assertThat(result).isNotNull();
        assertThat(result.getPasoActual()).isEqualTo(CompraFlowService.PASO_INICIADA);

        // La sesión huérfana debe cerrarse
        ArgumentCaptor<List<SesionCompra>> captorList = ArgumentCaptor.forClass(List.class);
        verify(sesionCompraRepository).saveAll(captorList.capture());
        List<SesionCompra> cerradas = captorList.getValue();
        assertThat(cerradas).hasSize(1);
        assertThat(cerradas.get(0).getActiva()).isFalse();
        assertThat(cerradas.get(0).getFechaExpiracion()).isNotNull();
    }

    /**
     * Test 2: getSesionActual cierra sesiones huérfanas si Redis está vacío.
     */
    @Test
    void getSesionActual_whenRedisEmpty_shouldCloseOrphanDbSessions() {
        // Given: Redis vacío pero hay sesión activa en DB
        when(compraSesionStore.get("testuser")).thenReturn(Optional.empty());

        SesionCompra sesionHuerfana = new SesionCompra();
        sesionHuerfana.setId(10L);
        sesionHuerfana.setActiva(true);

        when(sesionCompraRepository.findByUsuarioLoginAndActivaIsTrue("testuser")).thenReturn(List.of(sesionHuerfana));

        // When
        Optional<CompraSesionState> result = service.getSesionActual("testuser");

        // Then
        assertThat(result).isEmpty();
        verify(sesionCompraRepository).saveAll(anyList());
    }

    /**
     * Test 3: Retroceso de pasos - seleccionar asientos desde PASO_NOMBRES_CARGADOS
     * debe bajar a PASO_ASIENTOS_SELECCIONADOS y limpiar nombres.
     */
    @Test
    void seleccionarAsientos_fromPasoNombresCargados_shouldBacktrackAndClearNames() throws Exception {
        // Given: sesión en paso 4 (nombres cargados)
        SesionCompra sesionDb = new SesionCompra();
        sesionDb.setId(1L);
        sesionDb.setActiva(true);
        sesionDb.setPasoActual(CompraFlowService.PASO_NOMBRES_CARGADOS);
        sesionDb.setUsuario(mockUser);
        sesionDb.setEvento(mockEvento);
        sesionDb.setAsientosSeleccionados("[{\"fila\":1,\"columna\":1}]");
        sesionDb.setDatosPersonas("[{\"fila\":1,\"columna\":1,\"persona\":\"Juan\"}]");

        CompraSesionState redisState = new CompraSesionState();
        redisState.setSesionId(1L);
        redisState.setEventoIdLocal(1L);
        redisState.setEventoIdCatedra(100L);
        redisState.setPasoActual(CompraFlowService.PASO_NOMBRES_CARGADOS);
        redisState.setAsientosSeleccionadosJson("[{\"fila\":1,\"columna\":1}]");
        redisState.setDatosPersonasJson("[{\"fila\":1,\"columna\":1,\"persona\":\"Juan\"}]");

        when(compraSesionStore.get("testuser")).thenReturn(Optional.of(redisState));
        when(sesionCompraRepository.findById(1L)).thenReturn(Optional.of(sesionDb));
        when(proxyClientService.obtenerAsientos(100L, "jwt123")).thenReturn(new EventoAsientosDTO(List.of()));
        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"fila\":2,\"columna\":3}]");
        when(sesionCompraRepository.save(any(SesionCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        List<AsientoSeleccion> nuevaSeleccion = List.of(new AsientoSeleccion(2, 3));

        // When: seleccionar nuevos asientos (retroceso)
        CompraSesionState result = service.seleccionarAsientos(1L, nuevaSeleccion, "testuser", "jwt123");

        // Then
        assertThat(result.getPasoActual()).isEqualTo(CompraFlowService.PASO_ASIENTOS_SELECCIONADOS);
        assertThat(result.getDatosPersonasJson()).isNull(); // Nombres limpiados

        // Verificar que la DB se actualizó
        ArgumentCaptor<SesionCompra> captor = ArgumentCaptor.forClass(SesionCompra.class);
        verify(sesionCompraRepository).save(captor.capture());
        SesionCompra saved = captor.getValue();
        assertThat(saved.getPasoActual()).isEqualTo(CompraFlowService.PASO_ASIENTOS_SELECCIONADOS);
        assertThat(saved.getDatosPersonas()).isNull();
    }

    /**
     * Test 4: cargarNombres debe fallar si no se bloqueó antes (paso < 3).
     */
    @Test
    void cargarNombres_beforeBlocking_shouldFail() {
        // Given: sesión en paso 2 (asientos seleccionados pero no bloqueados)
        SesionCompra sesionDb = new SesionCompra();
        sesionDb.setId(1L);
        sesionDb.setActiva(true);
        sesionDb.setPasoActual(CompraFlowService.PASO_ASIENTOS_SELECCIONADOS);
        sesionDb.setUsuario(mockUser);
        sesionDb.setEvento(mockEvento);

        CompraSesionState redisState = new CompraSesionState();
        redisState.setSesionId(1L);
        redisState.setPasoActual(CompraFlowService.PASO_ASIENTOS_SELECCIONADOS);

        when(compraSesionStore.get("testuser")).thenReturn(Optional.of(redisState));
        when(sesionCompraRepository.findById(1L)).thenReturn(Optional.of(sesionDb));

        List<AsientoPersona> nombres = List.of(new AsientoPersona(1, 1, "Juan"));

        // When/Then: debe fallar porque no se bloqueó
        assertThrows(ResponseStatusException.class, () -> service.cargarNombres(1L, nombres, "testuser"));
    }

    /**
     * Test 5: iniciar con sesión existente en Redis del mismo evento debe tocar TTL y retornarla.
     */
    @Test
    void iniciar_whenRedisHasSameEvent_shouldReturnAndTouchTtl() {
        // Given: sesión existente en Redis para el mismo evento
        CompraSesionState existingState = new CompraSesionState();
        existingState.setSesionId(1L);
        existingState.setEventoIdLocal(1L);
        existingState.setEventoIdCatedra(100L);
        existingState.setPasoActual(CompraFlowService.PASO_ASIENTOS_SELECCIONADOS);

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(mockEvento));
        when(compraSesionStore.get("testuser")).thenReturn(Optional.of(existingState));

        // When
        CompraSesionState result = service.iniciar(1L, "testuser");

        // Then: debe retornar la sesión existente
        assertThat(result).isSameAs(existingState);
        verify(compraSesionStore, times(1)).save("testuser", existingState); // Touch TTL
        verify(sesionCompraRepository, never()).save(any()); // No se crea nueva
    }

    /**
     * Test 6: iniciar con sesión existente en Redis de otro evento debe cerrarla y crear nueva.
     */
    @Test
    void iniciar_whenRedisHasDifferentEvent_shouldCloseAndCreateNew() {
        // Given: sesión existente en Redis para otro evento
        CompraSesionState existingState = new CompraSesionState();
        existingState.setSesionId(1L);
        existingState.setEventoIdLocal(2L); // Otro evento
        existingState.setEventoIdCatedra(200L);

        SesionCompra sesionDbVieja = new SesionCompra();
        sesionDbVieja.setId(1L);
        sesionDbVieja.setActiva(true);
        sesionDbVieja.setUsuario(mockUser);
        sesionDbVieja.setEvento(mockEvento);

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(mockEvento));
        when(userRepository.findOneByLogin("testuser")).thenReturn(Optional.of(mockUser));
        when(compraSesionStore.get("testuser")).thenReturn(Optional.of(existingState));
        when(sesionCompraRepository.findById(1L)).thenReturn(Optional.of(sesionDbVieja));
        when(sesionCompraRepository.save(any(SesionCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CompraSesionState result = service.iniciar(1L, "testuser");

        // Then: se cerró la sesión anterior y se creó una nueva
        assertThat(result.getEventoIdLocal()).isEqualTo(1L);
        assertThat(result.getPasoActual()).isEqualTo(CompraFlowService.PASO_INICIADA);
        verify(compraSesionStore).delete("testuser"); // Cerró sesión anterior
        verify(sesionCompraRepository, times(2)).save(any()); // Una para cerrar, otra para crear
    }
}

