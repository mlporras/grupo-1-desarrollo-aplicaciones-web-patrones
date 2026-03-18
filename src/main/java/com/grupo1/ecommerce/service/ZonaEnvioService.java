package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.ZonaEnvio;
import com.grupo1.ecommerce.repository.ZonaEnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ZonaEnvioService {

    @Autowired
    private ZonaEnvioRepository zonaEnvioDao;

    public List<ZonaEnvio> getZonas() {
        return zonaEnvioDao.findAll();
    }

    public List<ZonaEnvio> getZonasEnvio(boolean soloActivas) {
        if (soloActivas) {
            return zonaEnvioDao.findByActivoTrue();
        }
        return zonaEnvioDao.findAll();
    }

    public Optional<ZonaEnvio> getZonaEnvio(Integer id) {
        return zonaEnvioDao.findById(id);
    }

    public void save(ZonaEnvio zonaEnvio) {
        zonaEnvioDao.save(zonaEnvio);
    }
}
