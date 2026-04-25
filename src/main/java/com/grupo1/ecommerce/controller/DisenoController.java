package com.grupo1.ecommerce.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo1.ecommerce.domain.DisenoTienda;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.service.DisenoTiendaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/diseno")
public class DisenoController {

    private final DisenoTiendaService disenoService;
    private final MessageSource messageSource;

    public DisenoController(DisenoTiendaService disenoService, MessageSource messageSource) {
        this.disenoService = disenoService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String editar(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        DisenoTienda diseno = disenoService.getBorrador(tienda)
                .orElse(disenoService.getPublicado(tienda)
                        .orElse(crearDefault(tienda)));

        model.addAttribute("diseno", diseno);
        return "/admin/diseno/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute DisenoTienda diseno,
                          @RequestParam(required = false) MultipartFile logoFile,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        if (logoFile != null && !logoFile.isEmpty()) {
            String tipo = logoFile.getContentType();
            if (tipo == null || (!tipo.equals("image/jpeg") && !tipo.equals("image/png"))) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("diseno.logo.formato", null, Locale.getDefault()));
                return "redirect:/admin/diseno";
            }
            if (logoFile.getSize() > 2 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("diseno.logo.tamano", null, Locale.getDefault()));
                return "redirect:/admin/diseno";
            }
        }

        diseno.setTienda(tienda);
        disenoService.guardarBorrador(diseno, logoFile);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("diseno.borrador.guardado", null, Locale.getDefault()));
        return "redirect:/admin/diseno";
    }

    @GetMapping("/preview")
    public String preview(HttpSession session, Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        DisenoTienda diseno = disenoService.getBorrador(tienda)
                .orElse(disenoService.getPublicado(tienda).orElse(crearDefault(tienda)));
        model.addAttribute("diseno", diseno);
        model.addAttribute("tienda", tienda);
        model.addAttribute("esPreview", true);
        return "/admin/diseno/preview";
    }

    @PostMapping("/preview")
    public String previewPost(@ModelAttribute DisenoTienda diseno,
                              @RequestParam(required = false) MultipartFile logoFile,
                              HttpSession session,
                              Model model) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        diseno.setTienda(tienda);
        DisenoTienda borrador = disenoService.guardarBorrador(diseno, logoFile);
        model.addAttribute("diseno", borrador);
        model.addAttribute("tienda", tienda);
        model.addAttribute("esPreview", true);
        return "/admin/diseno/preview";
    }

    @PostMapping("/publicar")
    public String publicar(HttpSession session, RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        try {
            disenoService.publicar(tienda);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("diseno.publicado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/diseno";
    }

    @PostMapping("/revertir")
    public String revertir(HttpSession session, RedirectAttributes redirectAttributes) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda == null) return "redirect:/admin/panel";

        disenoService.revertir(tienda);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("diseno.revertido", null, Locale.getDefault()));
        return "redirect:/admin/diseno";
    }

    private DisenoTienda crearDefault(Tienda tienda) {
        DisenoTienda d = new DisenoTienda();
        d.setTienda(tienda);
        return d;
    }
}
