package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.CarritoItemRepository;

@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final InventarioService inventarioService;

    public CarritoService(CarritoItemRepository carritoItemRepository, InventarioService inventarioService) {
        this.carritoItemRepository = carritoItemRepository;
        this.inventarioService = inventarioService;
    }

    // HU9: Obtener el stock disponible desde el InventarioService
    private int obtenerStockDisponible(Producto producto) {
        return inventarioService.getInventarioPorProducto(producto)
                .map(Inventario::getStock)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> getItemsPorUsuario(Usuario usuario) {
        return carritoItemRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
public double calcularTotal(Usuario usuario) {
    List<CarritoItem> items = getItemsPorUsuario(usuario);
    return items.stream()
            .mapToDouble(item -> item.getProducto().getPrecio()
                    .multiply(new java.math.BigDecimal(item.getCantidad()))
                    .doubleValue())
            .sum();
}

    @Transactional
    public void agregarOActualizar(Usuario usuario, Producto producto, int cantidad) {
        int stockDisponible = obtenerStockDisponible(producto);
        Optional<CarritoItem> itemExistente = getItemPorUsuarioYProducto(usuario, producto);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            
            if (nuevaCantidad <= stockDisponible) {
                item.setCantidad(nuevaCantidad);
                carritoItemRepository.save(item);
            }
        } else {
            if (cantidad <= stockDisponible) {
                CarritoItem nuevoItem = new CarritoItem();
                nuevoItem.setUsuario(usuario);
                nuevoItem.setProducto(producto);
                nuevoItem.setCantidad(cantidad);
                carritoItemRepository.save(nuevoItem);
            }
        }
    }

    @Transactional
    public void actualizarCantidad(Integer idItem, int nuevaCantidad) {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findById(idItem);
        if (itemOpt.isPresent()) {
            CarritoItem item = itemOpt.get();
            int stockDisponible = obtenerStockDisponible(item.getProducto());
            
            if (nuevaCantidad > 0 && nuevaCantidad <= stockDisponible) {
                item.setCantidad(nuevaCantidad);
                carritoItemRepository.save(item);
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<CarritoItem> getItemPorUsuarioYProducto(Usuario usuario, Producto producto) {
        return carritoItemRepository.findByUsuarioAndProducto(usuario, producto);
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