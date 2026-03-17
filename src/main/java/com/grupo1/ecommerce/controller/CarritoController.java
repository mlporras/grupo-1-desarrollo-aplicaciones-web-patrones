package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.service.CarritoService;
import com.grupo1.ecommerce.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tienda/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;

    public CarritoController(CarritoService carritoService, ProductoRepository productoRepository) {
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/auth/login";

        List<CarritoItem> items = carritoService.getItemsPorUsuario(usuario);
        model.addAttribute("items", items);
        model.addAttribute("totalGeneral", carritoService.calcularTotal(usuario));
        
        // Actualizar contador para el Navbar
        session.setAttribute("itemsCarritoCount", items.size());
        
        return "/tienda/carrito";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer idProducto, 
                                   @RequestParam int cantidad, 
                                   HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/auth/login";

        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            // CAMBIO: Se usa el nombre correcto del método en tu Service
            carritoService.agregarOActualizar(usuario, producto, cantidad);
            
            // Refrescar el contador de la sesión inmediatamente
            List<CarritoItem> items = carritoService.getItemsPorUsuario(usuario);
            session.setAttribute("itemsCarritoCount", items.size());
        }
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer idItem, 
                                     @RequestParam int cantidad) {
        carritoService.actualizarCantidad(idItem, cantidad);
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarItem(@PathVariable Integer id, HttpSession session) {
        // CAMBIO: Se usa el nombre correcto del método 'delete'
        carritoService.delete(id);
        
        // Actualizar contador tras eliminar
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            session.setAttribute("itemsCarritoCount", carritoService.getItemsPorUsuario(usuario).size());
        }
        return "redirect:/tienda/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciar(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            carritoService.vaciarCarrito(usuario);
            session.setAttribute("itemsCarritoCount", 0);
        }
        return "redirect:/tienda/carrito";
    }
}