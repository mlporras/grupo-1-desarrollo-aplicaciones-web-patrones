package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.ZonaEnvio;
import com.grupo1.ecommerce.service.ZonaEnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/envios")
public class ZonaEnvioController {

    @Autowired
    private ZonaEnvioService envioService;

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("zonas", envioService.getZonas());
        return "/admin/envios/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("zona", new ZonaEnvio());
        return "/admin/envios/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(ZonaEnvio zona) {
        envioService.save(zona);
        return "redirect:/admin/envios/listado";
    }
}
