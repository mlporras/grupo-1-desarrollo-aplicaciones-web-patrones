package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Categoria;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean soloActivos) {
        if (soloActivos) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductosPorCategoria(Categoria categoria, boolean soloActivos) {
        if (soloActivos) {
            return productoRepository.findByCategoriaAndActivoTrue(categoria);
        }
        return productoRepository.findByCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("El producto con ID " + id + " no existe.");
        }
        try {
            productoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
    }
}
