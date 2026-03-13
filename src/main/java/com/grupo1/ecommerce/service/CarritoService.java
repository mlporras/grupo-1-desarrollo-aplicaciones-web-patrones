package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.CarritoItemRepository;

@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;

    public CarritoService(CarritoItemRepository carritoItemRepository) {
        this.carritoItemRepository = carritoItemRepository;
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> getItemsPorUsuario(Usuario usuario) {
        return carritoItemRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<CarritoItem> getItem(Integer id) {
        return carritoItemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<CarritoItem> getItemPorUsuarioYProducto(Usuario usuario, Producto producto) {
        return carritoItemRepository.findByUsuarioAndProducto(usuario, producto);
    }

    @Transactional
    public void save(CarritoItem item) {
        carritoItemRepository.save(item);
    }

    @Transactional
    public void delete(Integer id) {
        carritoItemRepository.deleteById(id);
    }

    @Transactional
    public void vaciarCarrito(Usuario usuario) {
        carritoItemRepository.deleteByUsuario(usuario);
    }
}
