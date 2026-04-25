package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.repository.DetallePedidoRepository;
import com.grupo1.ecommerce.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ReporteService(PedidoRepository pedidoRepository,
                          DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getResumenGeneral() {
        Map<String, Object> resumen = new HashMap<>();
        BigDecimal totalVentas = pedidoRepository.sumTotalVentas();
        long cantidadPedidos = pedidoRepository.countPedidosActivos();
        BigDecimal ticketPromedio = cantidadPedidos > 0
            ? totalVentas.divide(BigDecimal.valueOf(cantidadPedidos), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        resumen.put("totalVentas", totalVentas);
        resumen.put("cantidadPedidos", cantidadPedidos);
        resumen.put("ticketPromedio", ticketPromedio);
        resumen.put("productosMasVendidos", getProductosMasVendidos());
        return resumen;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getResumenPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        Map<String, Object> resumen = new HashMap<>();
        BigDecimal totalVentas = pedidoRepository.sumTotalVentasPorFecha(desde, hasta);
        long cantidadPedidos = pedidoRepository.countPedidosActivosPorFecha(desde, hasta);
        BigDecimal ticketPromedio = cantidadPedidos > 0
            ? totalVentas.divide(BigDecimal.valueOf(cantidadPedidos), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        resumen.put("totalVentas", totalVentas);
        resumen.put("cantidadPedidos", cantidadPedidos);
        resumen.put("ticketPromedio", ticketPromedio);
        resumen.put("productosMasVendidos", getProductosMasVendidosPorFecha(desde, hasta));
        return resumen;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductosMasVendidos() {
        List<Object[]> rows = detallePedidoRepository.findProductosMasVendidos();
        return mapearProductos(rows);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductosMasVendidosPorFecha(
            LocalDateTime desde, LocalDateTime hasta) {
        List<Object[]> rows = detallePedidoRepository.findProductosMasVendidosPorFecha(desde, hasta);
        return mapearProductos(rows);
    }

    private List<Map<String, Object>> mapearProductos(List<Object[]> rows) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", row[0]);
            item.put("unidadesVendidas", row[1]);
            item.put("totalGenerado", row[2]);
            lista.add(item);
        }
        return lista;
    }
}