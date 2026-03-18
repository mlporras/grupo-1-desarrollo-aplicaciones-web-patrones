package com.grupo1.ecommerce.repository;

import com.grupo1.ecommerce.domain.Envio;
import com.grupo1.ecommerce.domain.ZonaEnvio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaEnvioRepository extends JpaRepository<Envio, Integer> {

    public void save(ZonaEnvio zonaEnvio);

    public List<ZonaEnvio> findByActivoTrue();
}