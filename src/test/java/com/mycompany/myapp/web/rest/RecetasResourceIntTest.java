package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.EcommerceApp;
import com.mycompany.myapp.domain.Recetas;
import com.mycompany.myapp.repository.RecetasRepository;
import com.mycompany.myapp.repository.search.RecetasSearchRepository;

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
 * Test class for the RecetasResource REST controller.
 *
 * @see RecetasResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EcommerceApp.class)
@WebAppConfiguration
@IntegrationTest
public class RecetasResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBB";

    private static final byte[] DEFAULT_IMAGEN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEN = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGEN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEN_CONTENT_TYPE = "image/png";

    @Inject
    private RecetasRepository recetasRepository;

    @Inject
    private RecetasSearchRepository recetasSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecetasMockMvc;

    private Recetas recetas;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecetasResource recetasResource = new RecetasResource();
        ReflectionTestUtils.setField(recetasResource, "recetasSearchRepository", recetasSearchRepository);
        ReflectionTestUtils.setField(recetasResource, "recetasRepository", recetasRepository);
        this.restRecetasMockMvc = MockMvcBuilders.standaloneSetup(recetasResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        recetasSearchRepository.deleteAll();
        recetas = new Recetas();
        recetas.setNombre(DEFAULT_NOMBRE);
        recetas.setDescripcion(DEFAULT_DESCRIPCION);
        recetas.setImagen(DEFAULT_IMAGEN);
        recetas.setImagenContentType(DEFAULT_IMAGEN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createRecetas() throws Exception {
        int databaseSizeBeforeCreate = recetasRepository.findAll().size();

        // Create the Recetas

        restRecetasMockMvc.perform(post("/api/recetas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recetas)))
                .andExpect(status().isCreated());

        // Validate the Recetas in the database
        List<Recetas> recetas = recetasRepository.findAll();
        assertThat(recetas).hasSize(databaseSizeBeforeCreate + 1);
        Recetas testRecetas = recetas.get(recetas.size() - 1);
        assertThat(testRecetas.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testRecetas.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testRecetas.getImagen()).isEqualTo(DEFAULT_IMAGEN);
        assertThat(testRecetas.getImagenContentType()).isEqualTo(DEFAULT_IMAGEN_CONTENT_TYPE);

        // Validate the Recetas in ElasticSearch
        Recetas recetasEs = recetasSearchRepository.findOne(testRecetas.getId());
        assertThat(recetasEs).isEqualToComparingFieldByField(testRecetas);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = recetasRepository.findAll().size();
        // set the field null
        recetas.setNombre(null);

        // Create the Recetas, which fails.

        restRecetasMockMvc.perform(post("/api/recetas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recetas)))
                .andExpect(status().isBadRequest());

        List<Recetas> recetas = recetasRepository.findAll();
        assertThat(recetas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescripcionIsRequired() throws Exception {
        int databaseSizeBeforeTest = recetasRepository.findAll().size();
        // set the field null
        recetas.setDescripcion(null);

        // Create the Recetas, which fails.

        restRecetasMockMvc.perform(post("/api/recetas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recetas)))
                .andExpect(status().isBadRequest());

        List<Recetas> recetas = recetasRepository.findAll();
        assertThat(recetas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecetas() throws Exception {
        // Initialize the database
        recetasRepository.saveAndFlush(recetas);

        // Get all the recetas
        restRecetasMockMvc.perform(get("/api/recetas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recetas.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
                .andExpect(jsonPath("$.[*].imagenContentType").value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].imagen").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGEN))));
    }

    @Test
    @Transactional
    public void getRecetas() throws Exception {
        // Initialize the database
        recetasRepository.saveAndFlush(recetas);

        // Get the recetas
        restRecetasMockMvc.perform(get("/api/recetas/{id}", recetas.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recetas.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()))
            .andExpect(jsonPath("$.imagenContentType").value(DEFAULT_IMAGEN_CONTENT_TYPE))
            .andExpect(jsonPath("$.imagen").value(Base64Utils.encodeToString(DEFAULT_IMAGEN)));
    }

    @Test
    @Transactional
    public void getNonExistingRecetas() throws Exception {
        // Get the recetas
        restRecetasMockMvc.perform(get("/api/recetas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecetas() throws Exception {
        // Initialize the database
        recetasRepository.saveAndFlush(recetas);
        recetasSearchRepository.save(recetas);
        int databaseSizeBeforeUpdate = recetasRepository.findAll().size();

        // Update the recetas
        Recetas updatedRecetas = new Recetas();
        updatedRecetas.setId(recetas.getId());
        updatedRecetas.setNombre(UPDATED_NOMBRE);
        updatedRecetas.setDescripcion(UPDATED_DESCRIPCION);
        updatedRecetas.setImagen(UPDATED_IMAGEN);
        updatedRecetas.setImagenContentType(UPDATED_IMAGEN_CONTENT_TYPE);

        restRecetasMockMvc.perform(put("/api/recetas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRecetas)))
                .andExpect(status().isOk());

        // Validate the Recetas in the database
        List<Recetas> recetas = recetasRepository.findAll();
        assertThat(recetas).hasSize(databaseSizeBeforeUpdate);
        Recetas testRecetas = recetas.get(recetas.size() - 1);
        assertThat(testRecetas.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testRecetas.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testRecetas.getImagen()).isEqualTo(UPDATED_IMAGEN);
        assertThat(testRecetas.getImagenContentType()).isEqualTo(UPDATED_IMAGEN_CONTENT_TYPE);

        // Validate the Recetas in ElasticSearch
        Recetas recetasEs = recetasSearchRepository.findOne(testRecetas.getId());
        assertThat(recetasEs).isEqualToComparingFieldByField(testRecetas);
    }

    @Test
    @Transactional
    public void deleteRecetas() throws Exception {
        // Initialize the database
        recetasRepository.saveAndFlush(recetas);
        recetasSearchRepository.save(recetas);
        int databaseSizeBeforeDelete = recetasRepository.findAll().size();

        // Get the recetas
        restRecetasMockMvc.perform(delete("/api/recetas/{id}", recetas.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean recetasExistsInEs = recetasSearchRepository.exists(recetas.getId());
        assertThat(recetasExistsInEs).isFalse();

        // Validate the database is empty
        List<Recetas> recetas = recetasRepository.findAll();
        assertThat(recetas).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRecetas() throws Exception {
        // Initialize the database
        recetasRepository.saveAndFlush(recetas);
        recetasSearchRepository.save(recetas);

        // Search the recetas
        restRecetasMockMvc.perform(get("/api/_search/recetas?query=id:" + recetas.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recetas.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
            .andExpect(jsonPath("$.[*].imagenContentType").value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGEN))));
    }
}
