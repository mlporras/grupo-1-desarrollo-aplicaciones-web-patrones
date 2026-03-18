package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.repository.InventarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioDao;

    public List<Inventario> getInventarios() {
        return inventarioDao.findAll();
    }

    public void save(Inventario inventario) {
        inventarioDao.save(inventario);
    }

    public Inventario getInventarioPorProducto(Producto producto) {
        return inventarioDao.findByProducto(producto);
    }
}