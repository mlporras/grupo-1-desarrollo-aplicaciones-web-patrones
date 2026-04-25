package com.grupo1.ecommerce.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.service.ColaboradorService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/colaboradores")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;
    private final MessageSource messageSource;

    public ColaboradorController(ColaboradorService colaboradorService, MessageSource messageSource) {
        this.colaboradorService = colaboradorService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        model.addAttribute("colaboradores", colaboradorService.getColaboradores(tienda));
        return "/admin/colaboradores/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo() {
        return "/admin/colaboradores/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
                          @RequestParam String correo,
                          @RequestParam String contrasena,
                          @RequestParam String rolColaborador,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        try {
            if (contrasena.length() < 8) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("auth.contrasena.invalida", null, Locale.getDefault()));
                return "redirect:/admin/colaboradores/nuevo";
            }
            colaboradorService.crearColaborador(nombre, correo, contrasena, rolColaborador, tienda);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("colaborador.creado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/colaboradores/listado";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        var colab = colaboradorService.getColaborador(id);
        if (colab.isEmpty()) return "redirect:/admin/colaboradores/listado";
        model.addAttribute("colaborador", colab.get());
        return "/admin/colaboradores/modifica";
    }

    @PostMapping("/actualizarRol")
    public String actualizarRol(@RequestParam Integer idColaborador,
                                @RequestParam String rolColaborador,
                                RedirectAttributes redirectAttributes) {
        try {
            colaboradorService.actualizarRol(idColaborador, rolColaborador);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("colaborador.rol.actualizado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/colaboradores/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            colaboradorService.eliminar(id);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("colaborador.eliminado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/colaboradores/listado";
    }
}
