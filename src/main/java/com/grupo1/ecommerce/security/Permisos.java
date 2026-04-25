package com.grupo1.ecommerce.security;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


public final class Permisos {

    private Permisos() {}

    public static final Set<String> MODULOS_SOLO_ADMIN = Set.of(
            "/admin/configuracion",
            "/admin/diseno",
            "/admin/colaboradores",
            "/admin/planes"
    );

    public static final Set<String> SUBRUTAS_ESCRITURA = Set.of(
            "/nuevo", "/editar", "/guardar", "/eliminar",
            "/activar", "/desactivar", "/actualizarRol"
    );

    public static boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("rolUsuario"));
    }

    public static boolean esColaborador(HttpSession session) {
        return "COLABORADOR".equals(session.getAttribute("rolUsuario"));
    }

    public static boolean esEditor(HttpSession session) {
        return esColaborador(session)
                && "EDITOR".equals(session.getAttribute("rolColaborador"));
    }

    public static boolean esViewer(HttpSession session) {
        return esColaborador(session)
                && "VIEWER".equals(session.getAttribute("rolColaborador"));
    }

    public static boolean esModuloSoloAdmin(String uri) {
        for (String m : MODULOS_SOLO_ADMIN) {
            if (uri.startsWith(m)) return true;
        }
        return false;
    }

    public static boolean esAccionEscritura(String uri) {
        for (String s : SUBRUTAS_ESCRITURA) {
            if (uri.contains(s)) return true;
        }
        return false;
    }

    public static boolean esMetodoMutante(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        return "POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method);
    }
}
