package com.grupo1.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/panel")
    public String panel(HttpSession session, Model model) {
        model.addAttribute("usuario", session.getAttribute("usuarioLogueado"));
        model.addAttribute("tienda", session.getAttribute("tienda"));
        return "/admin/panel";
    }
}
