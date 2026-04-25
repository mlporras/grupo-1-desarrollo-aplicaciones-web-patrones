package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Cupon;
import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.domain.ZonaEnvio;
import com.grupo1.ecommerce.service.CarritoService;
import com.grupo1.ecommerce.service.CuponService;
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
    private final CuponService cuponService;

    public CheckoutController(CarritoService carritoService, PedidoService pedidoService,
                              ZonaEnvioService zonaEnvioService, MetodoPagoService metodoPagoService,
                              CuponService cuponService) {
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
        this.zonaEnvioService = zonaEnvioService;
        this.metodoPagoService = metodoPagoService;
        this.cuponService = cuponService;
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
                                 @RequestParam(required = false) Integer idZona,
                                 @RequestParam(required = false) String codigoCupon,
                                 HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/auth/login";

        ZonaEnvio zona = null;
        java.math.BigDecimal costoEnvio = java.math.BigDecimal.ZERO;
        if (idZona != null) {
            zona = zonaEnvioService.getZonaEnvio(idZona).orElse(null);
            if (zona != null) {
                costoEnvio = zona.getCostoEnvio();
            }
        }

        double subtotalDbl = carritoService.calcularTotal(usuario);
        java.math.BigDecimal subtotal = java.math.BigDecimal.valueOf(subtotalDbl);
        java.math.BigDecimal descuento = java.math.BigDecimal.ZERO;
        Cupon cuponAplicado = null;

        if (codigoCupon != null && !codigoCupon.isBlank()) {
            var cuponOpt = cuponService.validarCupon(codigoCupon);
            if (cuponOpt.isPresent()) {
                cuponAplicado = cuponOpt.get();
                descuento = cuponService.calcularDescuento(cuponAplicado, subtotal);
                model.addAttribute("cupon", cuponAplicado);
            } else {
                model.addAttribute("cuponError", "Cupón inválido o expirado");
            }
        }

        java.math.BigDecimal totalFinal = subtotal.add(costoEnvio).subtract(descuento);

        model.addAttribute("items", carritoService.getItemsPorUsuario(usuario));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("descuento", descuento);
        model.addAttribute("totalFinal", totalFinal);
        model.addAttribute("direccion", direccion);
        model.addAttribute("idZona", idZona);
        model.addAttribute("zona", zona);
        model.addAttribute("codigoCupon", codigoCupon);
        model.addAttribute("metodosPago", metodoPagoService.getMetodosPago(true));

        return "/tienda/checkout/resumen";
    }

    @PostMapping("/confirmar")
    public String confirmarPedido(@RequestParam String direccion,
                                  @RequestParam String metodoPago,
                                  @RequestParam(required = false) Integer idZona,
                                  @RequestParam(required = false) String codigoCupon,
                                  HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/auth/login";

        try {
            Cupon cupon = null;
            if (codigoCupon != null && !codigoCupon.isBlank()) {
                cupon = cuponService.validarCupon(codigoCupon).orElse(null);
            }

            ZonaEnvio zona = null;
            if (idZona != null) {
                zona = zonaEnvioService.getZonaEnvio(idZona).orElse(null);
            }

            Pedido pedido = pedidoService.procesarCompra(usuario, direccion, metodoPago, cupon, zona);

            session.setAttribute("itemsCarritoCount", 0);
            model.addAttribute("pedido", pedido);
            return "/tienda/checkout/confirmacion";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/tienda/checkout/envio";
        }
    }
}
