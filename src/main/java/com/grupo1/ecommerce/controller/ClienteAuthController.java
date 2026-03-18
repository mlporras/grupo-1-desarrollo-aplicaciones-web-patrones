package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tienda") // Cambiado de /tienda/auth a /tienda
public class ClienteAuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Ruta final: /tienda/auth/registro
    @GetMapping("/auth/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/tienda/auth/registro";
    }

    // Ruta final: /tienda/auth/registro (POST)
    @PostMapping("/auth/registro")
    public String guardarRegistro(Usuario usuario) {
        usuarioService.registrarCliente(usuario);
        return "redirect:/auth/login"; // Redirige al login general
    }

    // Ruta final: /tienda/perfil
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/auth/login";
        }
        return "/tienda/perfil";
    }

    // Ruta final: /tienda/perfil/guardar
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(Usuario usuario, HttpSession session, RedirectAttributes redirectAttributes) {
        usuarioService.actualizarPerfil(usuario);
        
        // Refrescar sesión
        Usuario actualizado = usuarioService.getUsuario(usuario.getIdUsuario()).get();
        session.setAttribute("usuarioLogueado", actualizado);
        session.setAttribute("nombreUsuario", actualizado.getNombre());
        
        redirectAttributes.addFlashAttribute("todoOk", "Perfil actualizado correctamente");
        return "redirect:/tienda/perfil";
    }
}