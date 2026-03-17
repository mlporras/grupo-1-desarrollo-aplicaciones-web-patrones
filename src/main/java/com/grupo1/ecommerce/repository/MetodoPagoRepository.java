package com.grupo1.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.MetodoPago;
import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    List<MetodoPago> findByActivoTrue();
    
    Optional<MetodoPago> findByNombre(String nombre);
}
