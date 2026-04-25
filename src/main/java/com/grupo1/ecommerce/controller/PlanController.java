package com.grupo1.ecommerce.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.Plan;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.service.PlanService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/planes")
public class PlanController {

    private final PlanService planService;
    private final MessageSource messageSource;

    public PlanController(PlanService planService, MessageSource messageSource) {
        this.planService = planService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        model.addAttribute("planes", planService.getPlanesActivos());
        model.addAttribute("tienda", tienda);
        return "/admin/planes/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plan", new Plan());
        return "/admin/planes/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Plan plan, RedirectAttributes redirectAttributes) {
        try {
            planService.save(plan);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("plan.guardado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/planes/listado";
    }

    @PostMapping("/asignar")
    public String asignar(@RequestParam Integer idPlan,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        try {
            Tienda actualizada = planService.asignarPlan(tienda.getIdTienda(), idPlan);
            session.setAttribute("tienda", actualizada);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("plan.asignado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/planes/listado";
    }

    @PostMapping("/reactivar")
    public String reactivar(HttpSession session, RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        try {
            Tienda actualizada = planService.reactivar(tienda.getIdTienda());
            session.setAttribute("tienda", actualizada);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("plan.reactivado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/planes/listado";
    }

    @GetMapping("/suspendido")
    public String suspendido(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        model.addAttribute("tienda", tienda);
        return "/admin/planes/suspendido";
    }
}
