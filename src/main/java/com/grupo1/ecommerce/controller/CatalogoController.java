package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.service.ProductoService;
import com.grupo1.ecommerce.service.CategoriaService;
import com.grupo1.ecommerce.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.grupo1.ecommerce.domain.Categoria;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tienda")
public class CatalogoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;

    public CatalogoController(ProductoService productoService, 
                              CategoriaService categoriaService, 
                              InventarioService inventarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/catalogo")
public String listarCatalogo(@RequestParam(required = false) Integer idCategoria, Model model) {
    List<Producto> productos;
    
    if (idCategoria != null) {
        // Buscamos el objeto Categoria usando el idCategoria que recibimos
        Categoria cat = categoriaService.getCategoria(idCategoria).orElse(null);
        // Ahora sí, pasamos el objeto Categoria y el boolean true (solo activos)
        productos = productoService.getProductosPorCategoria(cat, true);
    } else {
        productos = productoService.getProductos(true);
    }

    // Filtrar solo productos que tengan stock > 0
    List<Producto> conStock = productos.stream()
            .filter(p -> inventarioService.getStock(p) > 0)
            .collect(Collectors.toList());

    model.addAttribute("productos", conStock);
    model.addAttribute("categorias", categoriaService.getCategorias(true));
    model.addAttribute("categoriaSeleccionada", idCategoria);
    return "/tienda/catalogo";
}

    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        Producto producto = productoService.getProducto(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        model.addAttribute("producto", producto);
        model.addAttribute("stock", inventarioService.getStock(producto));
        return "/tienda/detalle";
    }
}