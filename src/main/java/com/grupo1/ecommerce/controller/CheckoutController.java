package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.service.CarritoService;
import com.grupo1.ecommerce.service.MetodoPagoService;
import com.grupo1.ecommerce.service.PedidoService;
import com.grupo1.ecommerce.service.ZonaEnvioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tienda/checkout")
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final ZonaEnvioService zonaEnvioService;
    private final MetodoPagoService metodoPagoService;

    public CheckoutController(CarritoService carritoService, PedidoService pedidoService, 
                              ZonaEnvioService zonaEnvioService, MetodoPagoService metodoPagoService) {
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
        this.zonaEnvioService = zonaEnvioService;
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping("/envio")
    public String mostrarEnvio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/auth/login";
        
        if (carritoService.getItemsPorUsuario(usuario).isEmpty()) {
            return "redirect:/tienda/carrito";
        }

       model.addAttribute("zonas", zonaEnvioService.getZonasEnvio(true));
        return "/tienda/checkout/envio";
    }

    @PostMapping("/resumen")
    public String mostrarResumen(@RequestParam String direccion, 
                                 HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        model.addAttribute("items", carritoService.getItemsPorUsuario(usuario));
        model.addAttribute("total", carritoService.calcularTotal(usuario));
        model.addAttribute("direccion", direccion);
        model.addAttribute("metodosPago", metodoPagoService.getMetodosPago(true));
        
        return "/tienda/checkout/resumen";
    }

    @PostMapping("/confirmar")
    public String confirmarPedido(@RequestParam String direccion, 
                                  @RequestParam String metodoPago, 
                                  HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        try {
            Pedido pedido = pedidoService.procesarCompra(usuario, direccion, metodoPago);
            session.setAttribute("itemsCarritoCount", 0); // Limpiar contador navbar
            model.addAttribute("pedido", pedido);
            return "/tienda/checkout/confirmacion";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/tienda/carrito";
        }
    }
}