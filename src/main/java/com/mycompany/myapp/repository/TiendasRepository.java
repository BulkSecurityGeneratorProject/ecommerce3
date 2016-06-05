package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tiendas;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tiendas entity.
 */
@SuppressWarnings("unused")
public interface TiendasRepository extends JpaRepository<Tiendas,Long> {

}
