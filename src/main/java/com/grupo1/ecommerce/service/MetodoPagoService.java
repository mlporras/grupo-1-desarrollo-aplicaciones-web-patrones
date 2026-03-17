package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.MetodoPago;
import com.grupo1.ecommerce.repository.MetodoPagoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoDao;

    public List<MetodoPago> getMetodos(boolean activos){
        if(activos){
            return metodoPagoDao.findByActivoTrue();
        }
        return metodoPagoDao.findAll();
    }

    public void save(MetodoPago metodo){
        metodoPagoDao.save(metodo);
    }
}