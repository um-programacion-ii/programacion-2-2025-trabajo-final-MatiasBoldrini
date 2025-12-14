package com.eventos.pf.web.rest;

import static com.eventos.pf.domain.VentaAsserts.*;
import static com.eventos.pf.web.rest.TestUtil.createUpdateProxyForBean;
import static com.eventos.pf.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eventos.pf.IntegrationTest;
import com.eventos.pf.domain.Evento;
import com.eventos.pf.domain.User;
import com.eventos.pf.domain.Venta;
import com.eventos.pf.repository.UserRepository;
import com.eventos.pf.repository.VentaRepository;
import com.eventos.pf.service.VentaService;
import com.eventos.pf.service.dto.VentaDTO;
import com.eventos.pf.service.mapper.VentaMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VentaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VentaResourceIT {

    private static final Long DEFAULT_VENTA_ID_CATEDRA = 1L;
    private static final Long UPDATED_VENTA_ID_CATEDRA = 2L;
    private static final Long SMALLER_VENTA_ID_CATEDRA = 1L - 1L;

    private static final Instant DEFAULT_FECHA_VENTA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_VENTA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_PRECIO_VENTA = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO_VENTA = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRECIO_VENTA = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_RESULTADO = false;
    private static final Boolean UPDATED_RESULTADO = true;

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ventas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private VentaRepository ventaRepositoryMock;

    @Autowired
    private VentaMapper ventaMapper;

    @Mock
    private VentaService ventaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVentaMockMvc;

    private Venta venta;

    private Venta insertedVenta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venta createEntity() {
        return new Venta()
            .ventaIdCatedra(DEFAULT_VENTA_ID_CATEDRA)
            .fechaVenta(DEFAULT_FECHA_VENTA)
            .precioVenta(DEFAULT_PRECIO_VENTA)
            .resultado(DEFAULT_RESULTADO)
            .descripcion(DEFAULT_DESCRIPCION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venta createUpdatedEntity() {
        return new Venta()
            .ventaIdCatedra(UPDATED_VENTA_ID_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .resultado(UPDATED_RESULTADO)
            .descripcion(UPDATED_DESCRIPCION);
    }

    @BeforeEach
    void initTest() {
        venta = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVenta != null) {
            ventaRepository.delete(insertedVenta);
            insertedVenta = null;
        }
    }

    @Test
    @Transactional
    void createVenta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);
        var returnedVentaDTO = om.readValue(
            restVentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VentaDTO.class
        );

        // Validate the Venta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVenta = ventaMapper.toEntity(returnedVentaDTO);
        assertVentaUpdatableFieldsEquals(returnedVenta, getPersistedVenta(returnedVenta));

        insertedVenta = returnedVenta;
    }

    @Test
    @Transactional
    void createVentaWithExistingId() throws Exception {
        // Create the Venta with an existing ID
        venta.setId(1L);
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFechaVentaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        venta.setFechaVenta(null);

        // Create the Venta, which fails.
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVentas() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(venta.getId().intValue())))
            .andExpect(jsonPath("$.[*].ventaIdCatedra").value(hasItem(DEFAULT_VENTA_ID_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].fechaVenta").value(hasItem(DEFAULT_FECHA_VENTA.toString())))
            .andExpect(jsonPath("$.[*].precioVenta").value(hasItem(sameNumber(DEFAULT_PRECIO_VENTA))))
            .andExpect(jsonPath("$.[*].resultado").value(hasItem(DEFAULT_RESULTADO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentasWithEagerRelationshipsIsEnabled() throws Exception {
        when(ventaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ventaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ventaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ventaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get the venta
        restVentaMockMvc
            .perform(get(ENTITY_API_URL_ID, venta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(venta.getId().intValue()))
            .andExpect(jsonPath("$.ventaIdCatedra").value(DEFAULT_VENTA_ID_CATEDRA.intValue()))
            .andExpect(jsonPath("$.fechaVenta").value(DEFAULT_FECHA_VENTA.toString()))
            .andExpect(jsonPath("$.precioVenta").value(sameNumber(DEFAULT_PRECIO_VENTA)))
            .andExpect(jsonPath("$.resultado").value(DEFAULT_RESULTADO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    void getVentasByIdFiltering() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        Long id = venta.getId();

        defaultVentaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVentaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVentaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra equals to
        defaultVentaFiltering("ventaIdCatedra.equals=" + DEFAULT_VENTA_ID_CATEDRA, "ventaIdCatedra.equals=" + UPDATED_VENTA_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra in
        defaultVentaFiltering(
            "ventaIdCatedra.in=" + DEFAULT_VENTA_ID_CATEDRA + "," + UPDATED_VENTA_ID_CATEDRA,
            "ventaIdCatedra.in=" + UPDATED_VENTA_ID_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra is not null
        defaultVentaFiltering("ventaIdCatedra.specified=true", "ventaIdCatedra.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra is greater than or equal to
        defaultVentaFiltering(
            "ventaIdCatedra.greaterThanOrEqual=" + DEFAULT_VENTA_ID_CATEDRA,
            "ventaIdCatedra.greaterThanOrEqual=" + UPDATED_VENTA_ID_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra is less than or equal to
        defaultVentaFiltering(
            "ventaIdCatedra.lessThanOrEqual=" + DEFAULT_VENTA_ID_CATEDRA,
            "ventaIdCatedra.lessThanOrEqual=" + SMALLER_VENTA_ID_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra is less than
        defaultVentaFiltering("ventaIdCatedra.lessThan=" + UPDATED_VENTA_ID_CATEDRA, "ventaIdCatedra.lessThan=" + DEFAULT_VENTA_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllVentasByVentaIdCatedraIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where ventaIdCatedra is greater than
        defaultVentaFiltering(
            "ventaIdCatedra.greaterThan=" + SMALLER_VENTA_ID_CATEDRA,
            "ventaIdCatedra.greaterThan=" + DEFAULT_VENTA_ID_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta equals to
        defaultVentaFiltering("fechaVenta.equals=" + DEFAULT_FECHA_VENTA, "fechaVenta.equals=" + UPDATED_FECHA_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta in
        defaultVentaFiltering("fechaVenta.in=" + DEFAULT_FECHA_VENTA + "," + UPDATED_FECHA_VENTA, "fechaVenta.in=" + UPDATED_FECHA_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta is not null
        defaultVentaFiltering("fechaVenta.specified=true", "fechaVenta.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta equals to
        defaultVentaFiltering("precioVenta.equals=" + DEFAULT_PRECIO_VENTA, "precioVenta.equals=" + UPDATED_PRECIO_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta in
        defaultVentaFiltering(
            "precioVenta.in=" + DEFAULT_PRECIO_VENTA + "," + UPDATED_PRECIO_VENTA,
            "precioVenta.in=" + UPDATED_PRECIO_VENTA
        );
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta is not null
        defaultVentaFiltering("precioVenta.specified=true", "precioVenta.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta is greater than or equal to
        defaultVentaFiltering(
            "precioVenta.greaterThanOrEqual=" + DEFAULT_PRECIO_VENTA,
            "precioVenta.greaterThanOrEqual=" + UPDATED_PRECIO_VENTA
        );
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta is less than or equal to
        defaultVentaFiltering("precioVenta.lessThanOrEqual=" + DEFAULT_PRECIO_VENTA, "precioVenta.lessThanOrEqual=" + SMALLER_PRECIO_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta is less than
        defaultVentaFiltering("precioVenta.lessThan=" + UPDATED_PRECIO_VENTA, "precioVenta.lessThan=" + DEFAULT_PRECIO_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioVentaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioVenta is greater than
        defaultVentaFiltering("precioVenta.greaterThan=" + SMALLER_PRECIO_VENTA, "precioVenta.greaterThan=" + DEFAULT_PRECIO_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByResultadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where resultado equals to
        defaultVentaFiltering("resultado.equals=" + DEFAULT_RESULTADO, "resultado.equals=" + UPDATED_RESULTADO);
    }

    @Test
    @Transactional
    void getAllVentasByResultadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where resultado in
        defaultVentaFiltering("resultado.in=" + DEFAULT_RESULTADO + "," + UPDATED_RESULTADO, "resultado.in=" + UPDATED_RESULTADO);
    }

    @Test
    @Transactional
    void getAllVentasByResultadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where resultado is not null
        defaultVentaFiltering("resultado.specified=true", "resultado.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion equals to
        defaultVentaFiltering("descripcion.equals=" + DEFAULT_DESCRIPCION, "descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion in
        defaultVentaFiltering("descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION, "descripcion.in=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion is not null
        defaultVentaFiltering("descripcion.specified=true", "descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion contains
        defaultVentaFiltering("descripcion.contains=" + DEFAULT_DESCRIPCION, "descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion does not contain
        defaultVentaFiltering("descripcion.doesNotContain=" + UPDATED_DESCRIPCION, "descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByUsuarioIsEqualToSomething() throws Exception {
        User usuario;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            ventaRepository.saveAndFlush(venta);
            usuario = UserResourceIT.createEntity();
        } else {
            usuario = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(usuario);
        em.flush();
        venta.setUsuario(usuario);
        ventaRepository.saveAndFlush(venta);
        Long usuarioId = usuario.getId();
        // Get all the ventaList where usuario equals to usuarioId
        defaultVentaShouldBeFound("usuarioId.equals=" + usuarioId);

        // Get all the ventaList where usuario equals to (usuarioId + 1)
        defaultVentaShouldNotBeFound("usuarioId.equals=" + (usuarioId + 1));
    }

    @Test
    @Transactional
    void getAllVentasByEventoIsEqualToSomething() throws Exception {
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            ventaRepository.saveAndFlush(venta);
            evento = EventoResourceIT.createEntity();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        em.persist(evento);
        em.flush();
        venta.setEvento(evento);
        ventaRepository.saveAndFlush(venta);
        Long eventoId = evento.getId();
        // Get all the ventaList where evento equals to eventoId
        defaultVentaShouldBeFound("eventoId.equals=" + eventoId);

        // Get all the ventaList where evento equals to (eventoId + 1)
        defaultVentaShouldNotBeFound("eventoId.equals=" + (eventoId + 1));
    }

    private void defaultVentaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVentaShouldBeFound(shouldBeFound);
        defaultVentaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVentaShouldBeFound(String filter) throws Exception {
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(venta.getId().intValue())))
            .andExpect(jsonPath("$.[*].ventaIdCatedra").value(hasItem(DEFAULT_VENTA_ID_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].fechaVenta").value(hasItem(DEFAULT_FECHA_VENTA.toString())))
            .andExpect(jsonPath("$.[*].precioVenta").value(hasItem(sameNumber(DEFAULT_PRECIO_VENTA))))
            .andExpect(jsonPath("$.[*].resultado").value(hasItem(DEFAULT_RESULTADO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));

        // Check, that the count call also returns 1
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVentaShouldNotBeFound(String filter) throws Exception {
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVenta() throws Exception {
        // Get the venta
        restVentaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta
        Venta updatedVenta = ventaRepository.findById(venta.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVenta are not directly saved in db
        em.detach(updatedVenta);
        updatedVenta
            .ventaIdCatedra(UPDATED_VENTA_ID_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .resultado(UPDATED_RESULTADO)
            .descripcion(UPDATED_DESCRIPCION);
        VentaDTO ventaDTO = ventaMapper.toDto(updatedVenta);

        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVentaToMatchAllProperties(updatedVenta);
    }

    @Test
    @Transactional
    void putNonExistingVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVentaWithPatch() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta using partial update
        Venta partialUpdatedVenta = new Venta();
        partialUpdatedVenta.setId(venta.getId());

        partialUpdatedVenta.ventaIdCatedra(UPDATED_VENTA_ID_CATEDRA);

        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVenta))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVenta, venta), getPersistedVenta(venta));
    }

    @Test
    @Transactional
    void fullUpdateVentaWithPatch() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta using partial update
        Venta partialUpdatedVenta = new Venta();
        partialUpdatedVenta.setId(venta.getId());

        partialUpdatedVenta
            .ventaIdCatedra(UPDATED_VENTA_ID_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .resultado(UPDATED_RESULTADO)
            .descripcion(UPDATED_DESCRIPCION);

        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVenta))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaUpdatableFieldsEquals(partialUpdatedVenta, getPersistedVenta(partialUpdatedVenta));
    }

    @Test
    @Transactional
    void patchNonExistingVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ventaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the venta
        restVentaMockMvc
            .perform(delete(ENTITY_API_URL_ID, venta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ventaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Venta getPersistedVenta(Venta venta) {
        return ventaRepository.findById(venta.getId()).orElseThrow();
    }

    protected void assertPersistedVentaToMatchAllProperties(Venta expectedVenta) {
        assertVentaAllPropertiesEquals(expectedVenta, getPersistedVenta(expectedVenta));
    }

    protected void assertPersistedVentaToMatchUpdatableProperties(Venta expectedVenta) {
        assertVentaAllUpdatablePropertiesEquals(expectedVenta, getPersistedVenta(expectedVenta));
    }
}
