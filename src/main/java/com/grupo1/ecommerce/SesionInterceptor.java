package com.grupo1.ecommerce;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.security.Permisos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * MATRIZ DE PERMISOS (referencia para Naomi – HU11/HU12):
 *
 *  Rol           | Modulos accesibles                                       | Escritura
 *  --------------|----------------------------------------------------------|----------
 *  ADMIN         | Todos (/admin/**)                                        | Si
 *  COLABORADOR   | productos, inventario, pedidos, metodos, envios, cupones | Depende subrol
 *    EDITOR      | (mismos)                                                 | Si
 *    VIEWER      | (mismos, solo listados)                                  | No
 *  CLIENTE       | /tienda/** (no entra a /admin)                           | N/A
 *
 *  Modulos ADMIN-only: configuracion, diseno, colaboradores, planes
 *
 *  En vistas usar:
 *    th:with="puedeEscribir=${session.rolUsuario == 'ADMIN' or
 *             (session.rolUsuario == 'COLABORADOR' and session.rolColaborador == 'EDITOR')}"
 *
 *  Ver Permisos.java para helpers reutilizables.
 */
public class SesionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        String uri = request.getRequestURI();

        if (uri.startsWith("/admin/planes") || uri.startsWith("/auth/")
                || uri.equals("/acceso-denegado")) {
            return true;
        }

        Tienda tienda = (Tienda) session.getAttribute("tienda");
        if (tienda != null && "SUSPENDIDA".equals(tienda.getEstadoSuscripcion())) {
            response.sendRedirect("/admin/planes/suspendido");
            return false;
        }

        if (Permisos.esColaborador(session)) {
            if (Permisos.esModuloSoloAdmin(uri)) {
                response.sendRedirect("/acceso-denegado");
                return false;
            }

            if (Permisos.esViewer(session)) {
                if (Permisos.esAccionEscritura(uri) || Permisos.esMetodoMutante(request)) {
                    response.sendRedirect("/acceso-denegado");
                    return false;
                }
            }
        }

        return true;
    }
}
