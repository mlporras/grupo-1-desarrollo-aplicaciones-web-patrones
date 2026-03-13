package com.grupo1.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.ZonaEnvio;

public interface ZonaEnvioRepository extends JpaRepository<ZonaEnvio, Integer> {

    List<ZonaEnvio> findByActivoTrue();
}
