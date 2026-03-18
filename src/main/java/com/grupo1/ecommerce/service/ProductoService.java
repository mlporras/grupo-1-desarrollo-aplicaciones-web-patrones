package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoDao;

    @Autowired
    private InventarioService inventarioService;

    public List<Producto> getProductos(boolean activos) {
        if (activos) {
            return productoDao.findByActivoTrue();
        }
        return productoDao.findAll();
    }

    public Optional<Producto> getProducto(Integer id) {
        return productoDao.findById(id);
    }

    public void save(Producto producto) {

        // Guardar producto
        productoDao.save(producto);

        // Validar que tenga ID
        if (producto.getId() == null) {
            return;
        }

        // Buscar inventario existente
        Inventario inv = inventarioService.getInventarioPorProducto(producto);

        // Si no existe → crear
        if (inv == null) {
            Inventario nuevo = new Inventario();
            nuevo.setProducto(producto);
            nuevo.setStock(producto.getStock());
            nuevo.setStockMinimo(5);
            inventarioService.save(nuevo);
        } else {
            // Si ya existe → actualizar stock
            inv.setStock(producto.getStock());
            inventarioService.save(inv);
        }
    }

    public void delete(Integer id) {
        productoDao.deleteById(id);
    }
}