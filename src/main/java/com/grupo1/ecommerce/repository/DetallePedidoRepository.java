package com.grupo1.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.grupo1.ecommerce.domain.PedidoDetalle;
import com.grupo1.ecommerce.domain.Pedido;

public interface DetallePedidoRepository extends JpaRepository<PedidoDetalle, Integer> {

    List<PedidoDetalle> findByPedido(Pedido pedido);

    // Total de unidades vendidas y monto por producto
    @Query("SELECT d.producto.nombre, SUM(d.cantidad), SUM(d.subtotal) " +
           "FROM PedidoDetalle d " +
           "WHERE d.pedido.estado != 'CANCELADO' " +
           "GROUP BY d.producto.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> findProductosMasVendidos();

    // Para filtro por rango de fechas
    @Query("SELECT d.producto.nombre, SUM(d.cantidad), SUM(d.subtotal) " +
           "FROM PedidoDetalle d " +
           "WHERE d.pedido.estado != 'CANCELADO' " +
           "AND d.pedido.fechaPedido BETWEEN :desde AND :hasta " +
           "GROUP BY d.producto.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> findProductosMasVendidosPorFecha(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}