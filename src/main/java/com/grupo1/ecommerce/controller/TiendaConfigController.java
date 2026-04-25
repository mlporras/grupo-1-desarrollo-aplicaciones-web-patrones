package com.grupo1.ecommerce.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.service.TiendaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/configuracion")
public class TiendaConfigController {

    private final TiendaService tiendaService;
    private final MessageSource messageSource;

    public TiendaConfigController(TiendaService tiendaService, MessageSource messageSource) {
        this.tiendaService = tiendaService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String editar(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) {
            return "redirect:/admin/panel";
        }
        Tienda actual = tiendaService.getTienda(tienda.getIdTienda()).orElse(tienda);
        model.addAttribute("tienda", actual);
        return "/admin/configuracion/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Tienda tiendaForm,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            Tienda actualizada = tiendaService.guardarConfiguracion(tiendaForm);
            session.setAttribute("tienda", actualizada);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("config.actualizada", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/configuracion";
    }

    @PostMapping("/preview")
    public String preview(@ModelAttribute Tienda tiendaForm, Model model) {
        model.addAttribute("tienda", tiendaForm);
        model.addAttribute("esPreview", true);
        return "/admin/configuracion/preview";
    }

    @PostMapping("/restaurar")
    public String restaurar(HttpSession session, RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) {
            return "redirect:/admin/panel";
        }
        try {
            Tienda restaurada = tiendaService.restaurarConfiguracion(tienda.getIdTienda());
            session.setAttribute("tienda", restaurada);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("config.restaurada", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/configuracion";
    }
}
