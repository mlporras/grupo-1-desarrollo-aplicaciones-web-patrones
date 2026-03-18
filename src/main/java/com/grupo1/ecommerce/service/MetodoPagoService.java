package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.MetodoPago;
import com.grupo1.ecommerce.repository.MetodoPagoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> getMetodosPago(boolean soloActivos) {
        if (soloActivos) {
            return metodoPagoRepository.findByActivoTrue();
        }
        return metodoPagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MetodoPago> getMetodoPago(Integer id) {
        return metodoPagoRepository.findById(id);
    }

    @Transactional
    public void save(MetodoPago metodoPago) {
        metodoPagoRepository.save(metodoPago);
    }

    @Transactional
    public void delete(Integer id) {
        metodoPagoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<MetodoPago> getMetodoPorNombre(String nombre) {
        return metodoPagoRepository.findByNombre(nombre);
    }
}
