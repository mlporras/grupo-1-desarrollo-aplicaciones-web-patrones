package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.service.InventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String guardar(@RequestParam Integer idInventario,
                          @RequestParam Integer stock) {
        inventarioService.actualizarStockPorId(idInventario, stock);
        return "redirect:/admin/inventario/listado";
    }
}