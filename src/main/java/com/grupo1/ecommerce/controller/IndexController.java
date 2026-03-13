package com.grupo1.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.grupo1.ecommerce.service.ProductoService;

@Controller
public class IndexController {

    private final ProductoService productoService;

    public IndexController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String cargarPaginaInicio(Model model) {
        var productos = productoService.getProductos(true);
        model.addAttribute("productos", productos);
        return "/index";
    }
}
