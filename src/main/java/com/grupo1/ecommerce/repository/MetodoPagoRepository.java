package com.grupo1.ecommerce.repository;

import com.grupo1.ecommerce.domain.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    List<MetodoPago> findByActivoTrue();

    Optional<MetodoPago> findByNombre(String nombre);
}
