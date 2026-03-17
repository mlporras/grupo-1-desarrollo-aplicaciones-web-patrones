package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.Envio;
import com.grupo1.ecommerce.repository.ZonaEnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZonaEnvioService {
    @Autowired
    private ZonaEnvioRepository envioDao;
    public List<Envio> getEnvios(){
        return envioDao.findAll();
    }
    public void save(Envio envio){
        envioDao.save(envio);
    }

    public Object getZonas() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}