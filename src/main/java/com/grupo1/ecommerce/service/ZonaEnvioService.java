package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.ZonaEnvio;
import com.grupo1.ecommerce.repository.ZonaEnvioRepository;

@Service
public class ZonaEnvioService {

    private final ZonaEnvioRepository zonaEnvioRepository;

    public ZonaEnvioService(ZonaEnvioRepository zonaEnvioRepository) {
        this.zonaEnvioRepository = zonaEnvioRepository;
    }

    @Transactional(readOnly = true)
    public List<ZonaEnvio> getZonasEnvio(boolean soloActivas) {
        if (soloActivas) {
            return zonaEnvioRepository.findByActivoTrue();
        }
        return zonaEnvioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ZonaEnvio> getZonaEnvio(Integer id) {
        return zonaEnvioRepository.findById(id);
    }

    @Transactional
    public void save(ZonaEnvio zonaEnvio) {
        zonaEnvioRepository.save(zonaEnvio);
    }

    @Transactional
    public void delete(Integer id) {
        zonaEnvioRepository.deleteById(id);
    }
}
