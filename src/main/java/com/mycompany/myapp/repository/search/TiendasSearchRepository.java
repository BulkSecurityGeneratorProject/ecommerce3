package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Tiendas;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tiendas entity.
 */
public interface TiendasSearchRepository extends ElasticsearchRepository<Tiendas, Long> {
}
