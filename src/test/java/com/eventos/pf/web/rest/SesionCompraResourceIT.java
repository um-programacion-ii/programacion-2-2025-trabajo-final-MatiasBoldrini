package com.eventos.pf.web.rest;

import static com.eventos.pf.domain.SesionCompraAsserts.*;
import static com.eventos.pf.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eventos.pf.IntegrationTest;
import com.eventos.pf.domain.SesionCompra;
import com.eventos.pf.repository.SesionCompraRepository;
import com.eventos.pf.repository.UserRepository;
import com.eventos.pf.service.SesionCompraService;
import com.eventos.pf.service.dto.SesionCompraDTO;
import com.eventos.pf.service.mapper.SesionCompraMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link SesionCompraResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SesionCompraResourceIT {

    private static final Integer DEFAULT_PASO_ACTUAL = 1;
    private static final Integer UPDATED_PASO_ACTUAL = 2;

    private static final String DEFAULT_ASIENTOS_SELECCIONADOS = "AAAAAAAAAA";
    private static final String UPDATED_ASIENTOS_SELECCIONADOS = "BBBBBBBBBB";

    private static final String DEFAULT_DATOS_PERSONAS = "AAAAAAAAAA";
    private static final String UPDATED_DATOS_PERSONAS = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_EXPIRACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_EXPIRACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIVA = false;
    private static final Boolean UPDATED_ACTIVA = true;

    private static final String ENTITY_API_URL = "/api/sesion-compras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SesionCompraRepository sesionCompraRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SesionCompraRepository sesionCompraRepositoryMock;

    @Autowired
    private SesionCompraMapper sesionCompraMapper;

    @Mock
    private SesionCompraService sesionCompraServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSesionCompraMockMvc;

    private SesionCompra sesionCompra;

    private SesionCompra insertedSesionCompra;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SesionCompra createEntity() {
        return new SesionCompra()
            .pasoActual(DEFAULT_PASO_ACTUAL)
            .asientosSeleccionados(DEFAULT_ASIENTOS_SELECCIONADOS)
            .datosPersonas(DEFAULT_DATOS_PERSONAS)
            .fechaCreacion(DEFAULT_FECHA_CREACION)
            .fechaExpiracion(DEFAULT_FECHA_EXPIRACION)
            .activa(DEFAULT_ACTIVA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SesionCompra createUpdatedEntity() {
        return new SesionCompra()
            .pasoActual(UPDATED_PASO_ACTUAL)
            .asientosSeleccionados(UPDATED_ASIENTOS_SELECCIONADOS)
            .datosPersonas(UPDATED_DATOS_PERSONAS)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaExpiracion(UPDATED_FECHA_EXPIRACION)
            .activa(UPDATED_ACTIVA);
    }

    @BeforeEach
    void initTest() {
        sesionCompra = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSesionCompra != null) {
            sesionCompraRepository.delete(insertedSesionCompra);
            insertedSesionCompra = null;
        }
    }

    @Test
    @Transactional
    void createSesionCompra() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);
        var returnedSesionCompraDTO = om.readValue(
            restSesionCompraMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionCompraDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SesionCompraDTO.class
        );

        // Validate the SesionCompra in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSesionCompra = sesionCompraMapper.toEntity(returnedSesionCompraDTO);
        assertSesionCompraUpdatableFieldsEquals(returnedSesionCompra, getPersistedSesionCompra(returnedSesionCompra));

        insertedSesionCompra = returnedSesionCompra;
    }

    @Test
    @Transactional
    void createSesionCompraWithExistingId() throws Exception {
        // Create the SesionCompra with an existing ID
        sesionCompra.setId(1L);
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSesionCompraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionCompraDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPasoActualIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesionCompra.setPasoActual(null);

        // Create the SesionCompra, which fails.
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        restSesionCompraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionCompraDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesionCompra.setActiva(null);

        // Create the SesionCompra, which fails.
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        restSesionCompraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionCompraDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSesionCompras() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        // Get all the sesionCompraList
        restSesionCompraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sesionCompra.getId().intValue())))
            .andExpect(jsonPath("$.[*].pasoActual").value(hasItem(DEFAULT_PASO_ACTUAL)))
            .andExpect(jsonPath("$.[*].asientosSeleccionados").value(hasItem(DEFAULT_ASIENTOS_SELECCIONADOS)))
            .andExpect(jsonPath("$.[*].datosPersonas").value(hasItem(DEFAULT_DATOS_PERSONAS)))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())))
            .andExpect(jsonPath("$.[*].fechaExpiracion").value(hasItem(DEFAULT_FECHA_EXPIRACION.toString())))
            .andExpect(jsonPath("$.[*].activa").value(hasItem(DEFAULT_ACTIVA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSesionComprasWithEagerRelationshipsIsEnabled() throws Exception {
        when(sesionCompraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSesionCompraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sesionCompraServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSesionComprasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sesionCompraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSesionCompraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sesionCompraRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSesionCompra() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        // Get the sesionCompra
        restSesionCompraMockMvc
            .perform(get(ENTITY_API_URL_ID, sesionCompra.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sesionCompra.getId().intValue()))
            .andExpect(jsonPath("$.pasoActual").value(DEFAULT_PASO_ACTUAL))
            .andExpect(jsonPath("$.asientosSeleccionados").value(DEFAULT_ASIENTOS_SELECCIONADOS))
            .andExpect(jsonPath("$.datosPersonas").value(DEFAULT_DATOS_PERSONAS))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()))
            .andExpect(jsonPath("$.fechaExpiracion").value(DEFAULT_FECHA_EXPIRACION.toString()))
            .andExpect(jsonPath("$.activa").value(DEFAULT_ACTIVA));
    }

    @Test
    @Transactional
    void getNonExistingSesionCompra() throws Exception {
        // Get the sesionCompra
        restSesionCompraMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSesionCompra() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesionCompra
        SesionCompra updatedSesionCompra = sesionCompraRepository.findById(sesionCompra.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSesionCompra are not directly saved in db
        em.detach(updatedSesionCompra);
        updatedSesionCompra
            .pasoActual(UPDATED_PASO_ACTUAL)
            .asientosSeleccionados(UPDATED_ASIENTOS_SELECCIONADOS)
            .datosPersonas(UPDATED_DATOS_PERSONAS)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaExpiracion(UPDATED_FECHA_EXPIRACION)
            .activa(UPDATED_ACTIVA);
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(updatedSesionCompra);

        restSesionCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sesionCompraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sesionCompraDTO))
            )
            .andExpect(status().isOk());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSesionCompraToMatchAllProperties(updatedSesionCompra);
    }

    @Test
    @Transactional
    void putNonExistingSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sesionCompraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sesionCompraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sesionCompraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionCompraDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSesionCompraWithPatch() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesionCompra using partial update
        SesionCompra partialUpdatedSesionCompra = new SesionCompra();
        partialUpdatedSesionCompra.setId(sesionCompra.getId());

        partialUpdatedSesionCompra
            .pasoActual(UPDATED_PASO_ACTUAL)
            .asientosSeleccionados(UPDATED_ASIENTOS_SELECCIONADOS)
            .datosPersonas(UPDATED_DATOS_PERSONAS)
            .fechaExpiracion(UPDATED_FECHA_EXPIRACION);

        restSesionCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSesionCompra.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSesionCompra))
            )
            .andExpect(status().isOk());

        // Validate the SesionCompra in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSesionCompraUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSesionCompra, sesionCompra),
            getPersistedSesionCompra(sesionCompra)
        );
    }

    @Test
    @Transactional
    void fullUpdateSesionCompraWithPatch() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesionCompra using partial update
        SesionCompra partialUpdatedSesionCompra = new SesionCompra();
        partialUpdatedSesionCompra.setId(sesionCompra.getId());

        partialUpdatedSesionCompra
            .pasoActual(UPDATED_PASO_ACTUAL)
            .asientosSeleccionados(UPDATED_ASIENTOS_SELECCIONADOS)
            .datosPersonas(UPDATED_DATOS_PERSONAS)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaExpiracion(UPDATED_FECHA_EXPIRACION)
            .activa(UPDATED_ACTIVA);

        restSesionCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSesionCompra.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSesionCompra))
            )
            .andExpect(status().isOk());

        // Validate the SesionCompra in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSesionCompraUpdatableFieldsEquals(partialUpdatedSesionCompra, getPersistedSesionCompra(partialUpdatedSesionCompra));
    }

    @Test
    @Transactional
    void patchNonExistingSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sesionCompraDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sesionCompraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sesionCompraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSesionCompra() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesionCompra.setId(longCount.incrementAndGet());

        // Create the SesionCompra
        SesionCompraDTO sesionCompraDTO = sesionCompraMapper.toDto(sesionCompra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionCompraMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sesionCompraDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SesionCompra in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSesionCompra() throws Exception {
        // Initialize the database
        insertedSesionCompra = sesionCompraRepository.saveAndFlush(sesionCompra);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sesionCompra
        restSesionCompraMockMvc
            .perform(delete(ENTITY_API_URL_ID, sesionCompra.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sesionCompraRepository.count();
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

    protected SesionCompra getPersistedSesionCompra(SesionCompra sesionCompra) {
        return sesionCompraRepository.findById(sesionCompra.getId()).orElseThrow();
    }

    protected void assertPersistedSesionCompraToMatchAllProperties(SesionCompra expectedSesionCompra) {
        assertSesionCompraAllPropertiesEquals(expectedSesionCompra, getPersistedSesionCompra(expectedSesionCompra));
    }

    protected void assertPersistedSesionCompraToMatchUpdatableProperties(SesionCompra expectedSesionCompra) {
        assertSesionCompraAllUpdatablePropertiesEquals(expectedSesionCompra, getPersistedSesionCompra(expectedSesionCompra));
    }
}
