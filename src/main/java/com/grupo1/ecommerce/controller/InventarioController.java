package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.service.InventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/listado")
    public String listado(Model model){
        model.addAttribute("inventarios", inventarioService.getTodos());
        return "/admin/inventario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(Inventario inventario){
        inventarioService.save(inventario);
        return "redirect:/admin/inventario/listado";
    }
}