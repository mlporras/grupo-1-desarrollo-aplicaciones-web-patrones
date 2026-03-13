package com.grupo1.ecommerce.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public LoginController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "/auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contrasena,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.campo.obligatorio", null, Locale.getDefault()));
            return "redirect:/auth/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.login(correo, contrasena);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.credenciales.invalidas", null, Locale.getDefault()));
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.isActivo()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.cuenta.inactiva", null, Locale.getDefault()));
            return "redirect:/auth/login";
        }

        session.setAttribute("usuarioLogueado", usuario);

        Optional<Tienda> tiendaOpt = usuarioService.getTiendaPorUsuario(usuario);
        tiendaOpt.ifPresent(t -> session.setAttribute("tienda", t));

        if ("ADMIN".equals(usuario.getRol())) {
            return "redirect:/admin/panel";
        }
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "/auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String correo,
                                   @RequestParam String contrasena,
                                   @RequestParam String nombreTienda,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || nombreTienda.isBlank()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.campo.obligatorio", null, Locale.getDefault()));
            return "redirect:/auth/registro";
        }

        if (contrasena.length() < 8) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.contrasena.invalida", null, Locale.getDefault()));
            return "redirect:/auth/registro";
        }

        if (usuarioService.existeCorreo(correo)) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.correo.registrado", null, Locale.getDefault()));
            return "redirect:/auth/registro";
        }

        Usuario usuario = usuarioService.registrarEmprendedor(nombre, correo, contrasena, nombreTienda);
        session.setAttribute("usuarioLogueado", usuario);

        Optional<Tienda> tiendaOpt = usuarioService.getTiendaPorUsuario(usuario);
        tiendaOpt.ifPresent(t -> session.setAttribute("tienda", t));

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("auth.registro.exitoso", null, Locale.getDefault()));
        return "redirect:/admin/panel";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
