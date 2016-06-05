package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Producto;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Producto entity.
 */
@SuppressWarnings("unused")
public interface ProductoRepository extends JpaRepository<Producto,Long> {

    @Query("select distinct producto from Producto producto left join fetch producto.subcategorias")
    List<Producto> findAllWithEagerRelationships();

    @Query("select producto from Producto producto left join fetch producto.subcategorias where producto.id =:id")
    Producto findOneWithEagerRelationships(@Param("id") Long id);

}
