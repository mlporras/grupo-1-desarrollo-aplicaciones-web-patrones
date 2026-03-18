package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private MetodoPagoService metodoPagoService;

    @Autowired
    private ZonaEnvioService zonaEnvioService;

    // =========================
    // LISTADO
    // =========================
    @GetMapping("/listado")
    public String listado(Model model){
        model.addAttribute("pedidos", pedidoService.getTodosPedidos());
        return "/admin/pedidos/listado";
    }

    // =========================
    // NUEVO
    // =========================
    @GetMapping("/nuevo")
    public String nuevo(Model model){
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("metodos", metodoPagoService.getMetodosPago(true));
        model.addAttribute("zonas", zonaEnvioService.getZonas());
        return "/admin/pedidos/modifica";
    }

    // =========================
    // GUARDAR
    // =========================
    @PostMapping("/guardar")
    public String guardar(Pedido pedido){
        pedidoService.save(pedido);
        return "redirect:/admin/pedidos/listado";
    }
}