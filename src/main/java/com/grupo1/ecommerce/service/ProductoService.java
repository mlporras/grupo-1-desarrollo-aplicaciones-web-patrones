package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.Categoria;
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

    public List<Producto> getProductosPorCategoria(Categoria categoria, boolean soloActivos) {
        if (soloActivos) {
            return productoDao.findByCategoriaAndActivoTrue(categoria);
        }
        return productoDao.findByCategoria(categoria);
    }

    public void save(Producto producto) {
        productoDao.save(producto);

        if (producto.getIdProducto() == null) {
            return;
        }

        Optional<Inventario> invOpt = inventarioService.getInventarioPorProducto(producto);
        if (invOpt.isEmpty()) {
            Inventario nuevo = new Inventario();
            nuevo.setProducto(producto);
            nuevo.setStock(0);
            nuevo.setUmbralMinimo(5);
            inventarioService.save(nuevo);
        }
    }

    public void delete(Integer id) {
        productoDao.deleteById(id);
    }
}
