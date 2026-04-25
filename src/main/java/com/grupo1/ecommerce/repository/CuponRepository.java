package com.grupo1.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Cupon;

public interface CuponRepository extends JpaRepository<Cupon, Integer> {

    Optional<Cupon> findByCodigo(String codigo);

    List<Cupon> findByActivoTrue();

    boolean existsByCodigo(String codigo);
}
