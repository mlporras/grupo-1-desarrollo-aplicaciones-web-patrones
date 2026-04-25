package com.grupo1.ecommerce.controller;

import com.grupo1.ecommerce.service.ReporteService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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
    public ResponseEntity<byte[]> exportar(
            @RequestParam String formato,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false, defaultValue = "reporte") String nombre) {

        Map<String, Object> resumen = (desde != null && hasta != null)
            ? reporteService.getResumenPorFecha(desde.atStartOfDay(), hasta.atTime(23, 59, 59))
            : reporteService.getResumenGeneral();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productos =
            (List<Map<String, Object>>) resumen.get("productosMasVendidos");

        if ("csv".equalsIgnoreCase(formato)) {
            String csv = generarCsv(resumen, productos);
            byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreSeguro(nombre) + ".csv")
                    .body(bytes);
        } else if ("pdf".equalsIgnoreCase(formato)) {
            byte[] bytes = generarPdf(resumen, productos, desde, hasta);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreSeguro(nombre) + ".pdf")
                    .body(bytes);
        }

        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Formato no soportado. Use csv o pdf.".getBytes(StandardCharsets.UTF_8));
    }

    private String generarCsv(Map<String, Object> resumen, List<Map<String, Object>> productos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto,Unidades Vendidas,Total Generado\n");
        for (Map<String, Object> p : productos) {
            sb.append(csv(p.get("nombre"))).append(',')
              .append(csv(p.get("unidadesVendidas"))).append(',')
              .append(csv(p.get("totalGenerado"))).append('\n');
        }
        sb.append('\n');
        sb.append("Total Ventas,").append(csv(resumen.get("totalVentas"))).append('\n');
        sb.append("Cantidad Pedidos,").append(csv(resumen.get("cantidadPedidos"))).append('\n');
        sb.append("Ticket Promedio,").append(csv(resumen.get("ticketPromedio"))).append('\n');
        return sb.toString();
    }

    private String csv(Object value) {
        if (value == null) return "";
        String s = String.valueOf(value);
        boolean requiereComillas = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (requiereComillas) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private byte[] generarPdf(Map<String, Object> resumen,
                              List<Map<String, Object>> productos,
                              LocalDate desde,
                              LocalDate hasta) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("REPORTE DE VENTAS")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));

        if (desde != null && hasta != null) {
            doc.add(new Paragraph("Rango: " + desde + " a " + hasta)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10));
        }

        doc.add(new Paragraph(" "));

        DecimalFormat df = new DecimalFormat("#0.00");
        doc.add(new Paragraph("Total Ventas: \u20A1" + df.format(toNumber(resumen.get("totalVentas")))));
        doc.add(new Paragraph("Cantidad de Pedidos: " + String.valueOf(resumen.get("cantidadPedidos"))));
        doc.add(new Paragraph("Ticket Promedio: \u20A1" + df.format(toNumber(resumen.get("ticketPromedio")))));

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Productos mas vendidos").setBold());

        Table table = new Table(new float[]{6, 2, 3}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Unidades").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

        for (Map<String, Object> p : productos) {
            table.addCell(String.valueOf(p.get("nombre")));
            table.addCell(String.valueOf(p.get("unidadesVendidas")));
            table.addCell("\u20A1" + df.format(toNumber(p.get("totalGenerado"))));
        }

        doc.add(table);
        doc.close();

        return out.toByteArray();
    }

    private double toNumber(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String nombreSeguro(String nombre) {
        if (nombre == null || nombre.isBlank()) return "reporte";
        return nombre.replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}