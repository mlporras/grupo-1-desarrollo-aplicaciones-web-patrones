package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.MetodoPago;
import com.grupo1.ecommerce.service.MetodoPagoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/metodos")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @GetMapping("/listado")
    public String listado(Model model){
        model.addAttribute("metodos", metodoPagoService.getMetodosPago(false));
        return "/admin/metodos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){
        model.addAttribute("metodo", new MetodoPago());
        return "/admin/metodos/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(MetodoPago metodo){
        metodoPagoService.save(metodo);
        return "redirect:/admin/metodos/listado";
    }
}