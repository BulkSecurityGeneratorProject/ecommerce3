package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Tiendas;
import com.mycompany.myapp.repository.TiendasRepository;
import com.mycompany.myapp.repository.search.TiendasSearchRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Tiendas.
 */
@RestController
@RequestMapping("/api")
public class TiendasResource {

    private final Logger log = LoggerFactory.getLogger(TiendasResource.class);
        
    @Inject
    private TiendasRepository tiendasRepository;
    
    @Inject
    private TiendasSearchRepository tiendasSearchRepository;
    
    /**
     * POST  /tiendas : Create a new tiendas.
     *
     * @param tiendas the tiendas to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tiendas, or with status 400 (Bad Request) if the tiendas has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tiendas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tiendas> createTiendas(@Valid @RequestBody Tiendas tiendas) throws URISyntaxException {
        log.debug("REST request to save Tiendas : {}", tiendas);
        if (tiendas.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tiendas", "idexists", "A new tiendas cannot already have an ID")).body(null);
        }
        Tiendas result = tiendasRepository.save(tiendas);
        tiendasSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/tiendas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tiendas", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tiendas : Updates an existing tiendas.
     *
     * @param tiendas the tiendas to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tiendas,
     * or with status 400 (Bad Request) if the tiendas is not valid,
     * or with status 500 (Internal Server Error) if the tiendas couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tiendas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tiendas> updateTiendas(@Valid @RequestBody Tiendas tiendas) throws URISyntaxException {
        log.debug("REST request to update Tiendas : {}", tiendas);
        if (tiendas.getId() == null) {
            return createTiendas(tiendas);
        }
        Tiendas result = tiendasRepository.save(tiendas);
        tiendasSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tiendas", tiendas.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tiendas : get all the tiendas.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tiendas in body
     */
    @RequestMapping(value = "/tiendas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Tiendas> getAllTiendas() {
        log.debug("REST request to get all Tiendas");
        List<Tiendas> tiendas = tiendasRepository.findAll();
        return tiendas;
    }

    /**
     * GET  /tiendas/:id : get the "id" tiendas.
     *
     * @param id the id of the tiendas to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tiendas, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tiendas/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tiendas> getTiendas(@PathVariable Long id) {
        log.debug("REST request to get Tiendas : {}", id);
        Tiendas tiendas = tiendasRepository.findOne(id);
        return Optional.ofNullable(tiendas)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tiendas/:id : delete the "id" tiendas.
     *
     * @param id the id of the tiendas to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tiendas/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTiendas(@PathVariable Long id) {
        log.debug("REST request to delete Tiendas : {}", id);
        tiendasRepository.delete(id);
        tiendasSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tiendas", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tiendas?query=:query : search for the tiendas corresponding
     * to the query.
     *
     * @param query the query of the tiendas search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tiendas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Tiendas> searchTiendas(@RequestParam String query) {
        log.debug("REST request to search Tiendas for query {}", query);
        return StreamSupport
            .stream(tiendasSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
