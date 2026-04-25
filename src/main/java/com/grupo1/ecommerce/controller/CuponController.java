package com.grupo1.ecommerce.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.Cupon;
import com.grupo1.ecommerce.service.CuponService;

@Controller
@RequestMapping("/admin/cupones")
public class CuponController {

    private final CuponService cuponService;
    private final MessageSource messageSource;

    public CuponController(CuponService cuponService, MessageSource messageSource) {
        this.cuponService = cuponService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("cupones", cuponService.getCupones());
        return "/admin/cupones/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cupon", new Cupon());
        return "/admin/cupones/modifica";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Cupon> cuponOpt = cuponService.getCupon(id);
        if (cuponOpt.isEmpty()) return "redirect:/admin/cupones/listado";
        model.addAttribute("cupon", cuponOpt.get());
        return "/admin/cupones/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cupon cupon, RedirectAttributes redirectAttributes) {
        try {
            cuponService.save(cupon);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("cupon.guardado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cupones/listado";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            cuponService.desactivar(id);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("cupon.desactivado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cupones/listado";
    }
}
