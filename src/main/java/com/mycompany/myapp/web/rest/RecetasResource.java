package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Recetas;
import com.mycompany.myapp.repository.RecetasRepository;
import com.mycompany.myapp.repository.search.RecetasSearchRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Recetas.
 */
@RestController
@RequestMapping("/api")
public class RecetasResource {

    private final Logger log = LoggerFactory.getLogger(RecetasResource.class);
        
    @Inject
    private RecetasRepository recetasRepository;
    
    @Inject
    private RecetasSearchRepository recetasSearchRepository;
    
    /**
     * POST  /recetas : Create a new recetas.
     *
     * @param recetas the recetas to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recetas, or with status 400 (Bad Request) if the recetas has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recetas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recetas> createRecetas(@Valid @RequestBody Recetas recetas) throws URISyntaxException {
        log.debug("REST request to save Recetas : {}", recetas);
        if (recetas.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("recetas", "idexists", "A new recetas cannot already have an ID")).body(null);
        }
        Recetas result = recetasRepository.save(recetas);
        recetasSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/recetas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("recetas", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recetas : Updates an existing recetas.
     *
     * @param recetas the recetas to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recetas,
     * or with status 400 (Bad Request) if the recetas is not valid,
     * or with status 500 (Internal Server Error) if the recetas couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recetas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recetas> updateRecetas(@Valid @RequestBody Recetas recetas) throws URISyntaxException {
        log.debug("REST request to update Recetas : {}", recetas);
        if (recetas.getId() == null) {
            return createRecetas(recetas);
        }
        Recetas result = recetasRepository.save(recetas);
        recetasSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("recetas", recetas.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recetas : get all the recetas.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of recetas in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/recetas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recetas>> getAllRecetas(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Recetas");
        Page<Recetas> page = recetasRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recetas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /recetas/:id : get the "id" recetas.
     *
     * @param id the id of the recetas to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recetas, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/recetas/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recetas> getRecetas(@PathVariable Long id) {
        log.debug("REST request to get Recetas : {}", id);
        Recetas recetas = recetasRepository.findOne(id);
        return Optional.ofNullable(recetas)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /recetas/:id : delete the "id" recetas.
     *
     * @param id the id of the recetas to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/recetas/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRecetas(@PathVariable Long id) {
        log.debug("REST request to delete Recetas : {}", id);
        recetasRepository.delete(id);
        recetasSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("recetas", id.toString())).build();
    }

    /**
     * SEARCH  /_search/recetas?query=:query : search for the recetas corresponding
     * to the query.
     *
     * @param query the query of the recetas search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/recetas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recetas>> searchRecetas(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Recetas for query {}", query);
        Page<Recetas> page = recetasSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/recetas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
