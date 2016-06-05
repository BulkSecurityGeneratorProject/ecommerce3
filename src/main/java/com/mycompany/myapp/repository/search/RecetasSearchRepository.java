package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Recetas;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Recetas entity.
 */
public interface RecetasSearchRepository extends ElasticsearchRepository<Recetas, Long> {
}
