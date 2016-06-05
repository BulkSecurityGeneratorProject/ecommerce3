package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.EcommerceApp;
import com.mycompany.myapp.domain.Tiendas;
import com.mycompany.myapp.repository.TiendasRepository;
import com.mycompany.myapp.repository.search.TiendasSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TiendasResource REST controller.
 *
 * @see TiendasResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EcommerceApp.class)
@WebAppConfiguration
@IntegrationTest
public class TiendasResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    private static final String DEFAULT_DIRECCION = "AAAAA";
    private static final String UPDATED_DIRECCION = "BBBBB";
    private static final String DEFAULT_TELEFONO = "AAAAA";
    private static final String UPDATED_TELEFONO = "BBBBB";
    private static final String DEFAULT_HORARIO = "AAAAA";
    private static final String UPDATED_HORARIO = "BBBBB";

    @Inject
    private TiendasRepository tiendasRepository;

    @Inject
    private TiendasSearchRepository tiendasSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTiendasMockMvc;

    private Tiendas tiendas;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TiendasResource tiendasResource = new TiendasResource();
        ReflectionTestUtils.setField(tiendasResource, "tiendasSearchRepository", tiendasSearchRepository);
        ReflectionTestUtils.setField(tiendasResource, "tiendasRepository", tiendasRepository);
        this.restTiendasMockMvc = MockMvcBuilders.standaloneSetup(tiendasResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tiendasSearchRepository.deleteAll();
        tiendas = new Tiendas();
        tiendas.setNombre(DEFAULT_NOMBRE);
        tiendas.setDireccion(DEFAULT_DIRECCION);
        tiendas.setTelefono(DEFAULT_TELEFONO);
        tiendas.setHorario(DEFAULT_HORARIO);
    }

    @Test
    @Transactional
    public void createTiendas() throws Exception {
        int databaseSizeBeforeCreate = tiendasRepository.findAll().size();

        // Create the Tiendas

        restTiendasMockMvc.perform(post("/api/tiendas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tiendas)))
                .andExpect(status().isCreated());

        // Validate the Tiendas in the database
        List<Tiendas> tiendas = tiendasRepository.findAll();
        assertThat(tiendas).hasSize(databaseSizeBeforeCreate + 1);
        Tiendas testTiendas = tiendas.get(tiendas.size() - 1);
        assertThat(testTiendas.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testTiendas.getDireccion()).isEqualTo(DEFAULT_DIRECCION);
        assertThat(testTiendas.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
        assertThat(testTiendas.getHorario()).isEqualTo(DEFAULT_HORARIO);

        // Validate the Tiendas in ElasticSearch
        Tiendas tiendasEs = tiendasSearchRepository.findOne(testTiendas.getId());
        assertThat(tiendasEs).isEqualToComparingFieldByField(testTiendas);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = tiendasRepository.findAll().size();
        // set the field null
        tiendas.setNombre(null);

        // Create the Tiendas, which fails.

        restTiendasMockMvc.perform(post("/api/tiendas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tiendas)))
                .andExpect(status().isBadRequest());

        List<Tiendas> tiendas = tiendasRepository.findAll();
        assertThat(tiendas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDireccionIsRequired() throws Exception {
        int databaseSizeBeforeTest = tiendasRepository.findAll().size();
        // set the field null
        tiendas.setDireccion(null);

        // Create the Tiendas, which fails.

        restTiendasMockMvc.perform(post("/api/tiendas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tiendas)))
                .andExpect(status().isBadRequest());

        List<Tiendas> tiendas = tiendasRepository.findAll();
        assertThat(tiendas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTiendas() throws Exception {
        // Initialize the database
        tiendasRepository.saveAndFlush(tiendas);

        // Get all the tiendas
        restTiendasMockMvc.perform(get("/api/tiendas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tiendas.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION.toString())))
                .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO.toString())))
                .andExpect(jsonPath("$.[*].horario").value(hasItem(DEFAULT_HORARIO.toString())));
    }

    @Test
    @Transactional
    public void getTiendas() throws Exception {
        // Initialize the database
        tiendasRepository.saveAndFlush(tiendas);

        // Get the tiendas
        restTiendasMockMvc.perform(get("/api/tiendas/{id}", tiendas.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tiendas.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.direccion").value(DEFAULT_DIRECCION.toString()))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO.toString()))
            .andExpect(jsonPath("$.horario").value(DEFAULT_HORARIO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTiendas() throws Exception {
        // Get the tiendas
        restTiendasMockMvc.perform(get("/api/tiendas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTiendas() throws Exception {
        // Initialize the database
        tiendasRepository.saveAndFlush(tiendas);
        tiendasSearchRepository.save(tiendas);
        int databaseSizeBeforeUpdate = tiendasRepository.findAll().size();

        // Update the tiendas
        Tiendas updatedTiendas = new Tiendas();
        updatedTiendas.setId(tiendas.getId());
        updatedTiendas.setNombre(UPDATED_NOMBRE);
        updatedTiendas.setDireccion(UPDATED_DIRECCION);
        updatedTiendas.setTelefono(UPDATED_TELEFONO);
        updatedTiendas.setHorario(UPDATED_HORARIO);

        restTiendasMockMvc.perform(put("/api/tiendas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTiendas)))
                .andExpect(status().isOk());

        // Validate the Tiendas in the database
        List<Tiendas> tiendas = tiendasRepository.findAll();
        assertThat(tiendas).hasSize(databaseSizeBeforeUpdate);
        Tiendas testTiendas = tiendas.get(tiendas.size() - 1);
        assertThat(testTiendas.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testTiendas.getDireccion()).isEqualTo(UPDATED_DIRECCION);
        assertThat(testTiendas.getTelefono()).isEqualTo(UPDATED_TELEFONO);
        assertThat(testTiendas.getHorario()).isEqualTo(UPDATED_HORARIO);

        // Validate the Tiendas in ElasticSearch
        Tiendas tiendasEs = tiendasSearchRepository.findOne(testTiendas.getId());
        assertThat(tiendasEs).isEqualToComparingFieldByField(testTiendas);
    }

    @Test
    @Transactional
    public void deleteTiendas() throws Exception {
        // Initialize the database
        tiendasRepository.saveAndFlush(tiendas);
        tiendasSearchRepository.save(tiendas);
        int databaseSizeBeforeDelete = tiendasRepository.findAll().size();

        // Get the tiendas
        restTiendasMockMvc.perform(delete("/api/tiendas/{id}", tiendas.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tiendasExistsInEs = tiendasSearchRepository.exists(tiendas.getId());
        assertThat(tiendasExistsInEs).isFalse();

        // Validate the database is empty
        List<Tiendas> tiendas = tiendasRepository.findAll();
        assertThat(tiendas).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTiendas() throws Exception {
        // Initialize the database
        tiendasRepository.saveAndFlush(tiendas);
        tiendasSearchRepository.save(tiendas);

        // Search the tiendas
        restTiendasMockMvc.perform(get("/api/_search/tiendas?query=id:" + tiendas.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tiendas.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION.toString())))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO.toString())))
            .andExpect(jsonPath("$.[*].horario").value(hasItem(DEFAULT_HORARIO.toString())));
    }
}
