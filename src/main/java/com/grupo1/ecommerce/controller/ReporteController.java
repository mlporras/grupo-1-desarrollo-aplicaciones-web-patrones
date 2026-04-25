package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String verReporte(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        Map<String, Object> resumen;

        if (desde != null && hasta != null) {
            LocalDateTime desdeTime = desde.atStartOfDay();
            LocalDateTime hastaTime = hasta.atTime(23, 59, 59);
            resumen = reporteService.getResumenPorFecha(desdeTime, hastaTime);
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
        } else {
            resumen = reporteService.getResumenGeneral();
        }

        model.addAttribute("totalVentas", resumen.get("totalVentas"));
        model.addAttribute("cantidadPedidos", resumen.get("cantidadPedidos"));
        model.addAttribute("ticketPromedio", resumen.get("ticketPromedio"));
        model.addAttribute("productos", resumen.get("productosMasVendidos"));

        return "/admin/reportes/reporte";
    }

    @GetMapping("/exportar")
    @ResponseBody
    public void exportar(
            @RequestParam String formato,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletResponse response) throws Exception {

        Map<String, Object> resumen = (desde != null && hasta != null)
            ? reporteService.getResumenPorFecha(desde.atStartOfDay(), hasta.atTime(23, 59, 59))
            : reporteService.getResumenGeneral();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productos =
            (List<Map<String, Object>>) resumen.get("productosMasVendidos");

        if ("csv".equalsIgnoreCase(formato)) {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=reporte.csv");
            java.io.PrintWriter writer = response.getWriter();

            writer.println("Producto,Unidades Vendidas,Total Generado");
            for (Map<String, Object> p : productos) {
                writer.println(p.get("nombre") + "," +
                               p.get("unidadesVendidas") + "," +
                               p.get("totalGenerado"));
            }
            writer.println();
            writer.println("Total Ventas," + resumen.get("totalVentas"));
            writer.println("Cantidad Pedidos," + resumen.get("cantidadPedidos"));
            writer.println("Ticket Promedio," + resumen.get("ticketPromedio"));
            writer.flush();

        } else if ("pdf".equalsIgnoreCase(formato)) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=reporte.txt");
            java.io.PrintWriter writer = response.getWriter();

            writer.println("===== REPORTE DE VENTAS =====");
            writer.println();
            writer.println("Total Ventas: " + resumen.get("totalVentas"));
            writer.println("Cantidad Pedidos: " + resumen.get("cantidadPedidos"));
            writer.println("Ticket Promedio: " + resumen.get("ticketPromedio"));
            writer.println();
            writer.println("--- Productos Mas Vendidos ---");
            writer.println("Producto | Unidades | Total");
            for (Map<String, Object> p : productos) {
                writer.println(p.get("nombre") + " | " +
                               p.get("unidadesVendidas") + " | " +
                               p.get("totalGenerado"));
            }
            writer.flush();
        }
    }
}