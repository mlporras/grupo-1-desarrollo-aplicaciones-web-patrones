package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.service.ProductoService;
import com.grupo1.ecommerce.service.CategoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    // =========================
    // LISTADO
    // =========================
    @GetMapping("/listado")
    public String listado(Model model){
        model.addAttribute("productos", productoService.getProductos(true));
        return "/admin/productos/listado";
    }

    // =========================
    // NUEVO
    // =========================
    @GetMapping("/nuevo")
    public String nuevo(Model model){
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/admin/productos/modifica";
    }

    // =========================
    // GUARDAR (CREAR / EDITAR)
    // =========================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, Model model){
        try {
            productoService.save(producto);
            return "redirect:/admin/productos/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el producto");
            model.addAttribute("categorias", categoriaService.getCategorias(true));
            return "/admin/productos/modifica";
        }
    }

    // =========================
    // EDITAR
    // =========================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model){
        Optional<Producto> productoOpt = productoService.getProducto(id);

        if(productoOpt.isPresent()){
            model.addAttribute("producto", productoOpt.get());
            model.addAttribute("categorias", categoriaService.getCategorias(true));
            return "/admin/productos/modifica";
        }

        return "redirect:/admin/productos/listado";
    }

    // =========================
    // ELIMINAR
    // =========================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id){
        try {
            productoService.delete(id);
        } catch (Exception e) {
            // opcional: manejo de error
        }
        return "redirect:/admin/productos/listado";
    }

    // =========================
    // ACTIVAR / DESACTIVAR
    // =========================
    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Integer id){
        Optional<Producto> productoOpt = productoService.getProducto(id);

        if(productoOpt.isPresent()){
            Producto producto = productoOpt.get();
            producto.setActivo(!producto.isActivo());
            productoService.save(producto);
        }

        return "redirect:/admin/productos/listado";
    }
}