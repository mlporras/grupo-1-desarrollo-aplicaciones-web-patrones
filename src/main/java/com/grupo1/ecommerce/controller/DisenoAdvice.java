package com.grupo1.ecommerce.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.grupo1.ecommerce.domain.DisenoTienda;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.service.DisenoTiendaService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class DisenoAdvice {

    private final DisenoTiendaService disenoService;

    public DisenoAdvice(DisenoTiendaService disenoService) {
        this.disenoService = disenoService;
    }

    @ModelAttribute("disenoPublicado")
    public DisenoTienda disenoPublicado(HttpSession session) {
        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda != null) {
            return disenoService.getPublicado(tienda).orElse(null);
        }
        return null;
    }
}
