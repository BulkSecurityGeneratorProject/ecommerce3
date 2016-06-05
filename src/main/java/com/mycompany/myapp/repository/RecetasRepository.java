package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recetas;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Recetas entity.
 */
@SuppressWarnings("unused")
public interface RecetasRepository extends JpaRepository<Recetas,Long> {

}
