package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private MetodoPagoService metodoPagoService;
    @Autowired
    private ZonaEnvioService zonaEnvioService;
    @Autowired
    private UsuarioService usuarioService;

    // LISTADO
    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("pedidos", pedidoService.getTodosPedidos());
        return "/admin/pedidos/listado";
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("usuarios", usuarioService.getClientesActivos());
        model.addAttribute("metodos", metodoPagoService.getMetodosPago(true));
        model.addAttribute("zonas", zonaEnvioService.getZonas());
        return "/admin/pedidos/modifica";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(Pedido pedido) {
        pedidoService.save(pedido);
        return "redirect:/admin/pedidos/listado";
    }

    // VER DETALLE (HU11)
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        return pedidoService.getPedido(id).map(p -> {
            model.addAttribute("pedido", p);
            return "/admin/pedidos/detalle";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/admin/pedidos/listado";
        });
    }

    // FORM CAMBIAR ESTADO (HU11)
    @GetMapping("/estado/{id}")
    public String formEstado(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        return pedidoService.getPedido(id).map(p -> {
            model.addAttribute("pedido", p);
            return "/admin/pedidos/cambiarEstado";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/admin/pedidos/listado";
        });
    }

    // GUARDAR NUEVO ESTADO (HU11)
    @PostMapping("/estado/guardar")
    public String guardarEstado(@RequestParam Integer idPedido,
                                 @RequestParam String estado,
                                 RedirectAttributes ra) {
        try {
            pedidoService.cambiarEstado(idPedido, estado);
            ra.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos/listado";
    }

    // CANCELAR PEDIDO (HU11)
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            pedidoService.cancelarPedido(id);
            ra.addFlashAttribute("mensaje", "Pedido cancelado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos/listado";
    }
}