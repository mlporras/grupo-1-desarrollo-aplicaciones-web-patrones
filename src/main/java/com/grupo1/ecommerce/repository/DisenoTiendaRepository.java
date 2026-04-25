package com.grupo1.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.DisenoTienda;
import com.grupo1.ecommerce.domain.Tienda;

public interface DisenoTiendaRepository extends JpaRepository<DisenoTienda, Integer> {

    Optional<DisenoTienda> findByTiendaAndBorradorFalse(Tienda tienda);

    Optional<DisenoTienda> findByTiendaAndBorradorTrue(Tienda tienda);
}
