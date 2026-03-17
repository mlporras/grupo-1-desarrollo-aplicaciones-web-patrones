package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.repository.InventarioRepository;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Inventario> getTodos() {
        return inventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Inventario> getInventarioPorProducto(Producto producto) {
        return inventarioRepository.findByProducto(producto);
    }

    // HU9/HU10: Método auxiliar para obtener solo el número de stock
    @Transactional(readOnly = true)
    public int getStock(Producto producto) {
        return getInventarioPorProducto(producto)
                .map(Inventario::getStock)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public List<Inventario> getStockBajo() {
        return inventarioRepository.findStockBajo();
    }

    @Transactional
    public void save(Inventario inventario) {
        inventarioRepository.save(inventario);
    }

    @Transactional
    public void actualizarStock(Producto producto, int nuevoStock) {
        Inventario inv = inventarioRepository.findByProducto(producto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto."));
        inv.setStock(nuevoStock);
        inventarioRepository.save(inv);
    }

    // --- NUEVO MÉTODO PARA HU10 (Checkout) ---
    @Transactional
    public void descontarStock(Producto producto, int cantidad) {
        Inventario inv = inventarioRepository.findByProducto(producto)
                .orElseThrow(() -> new IllegalArgumentException("No existe registro de inventario para: " + producto.getNombre()));

        if (inv.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        int nuevoStock = inv.getStock() - cantidad;
        inv.setStock(nuevoStock);
        
        inventarioRepository.save(inv);
    }
}