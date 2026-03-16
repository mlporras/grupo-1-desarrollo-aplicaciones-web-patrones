package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tienda/auth")
public class ClienteAuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String registro(Model model) {
        // Preparamos un objeto Usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());
        return "/tienda/auth/registro";
    }

    @PostMapping("/registro")
    public String guardarRegistro(Usuario usuario) {
        // Usamos el método adaptado en el paso anterior que asigna el rol "CLIENTE"
        usuarioService.registrarCliente(usuario);
        // Redirigimos al login tras el éxito
        return "redirect:/tienda/auth/login";
    }

    @GetMapping("/login")
    public String login() {
        return "/tienda/auth/login";
    }
}