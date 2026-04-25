package com.grupo1.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario(Usuario usuario);
    List<Pedido> findByEstado(String estado);
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findAllByOrderByFechaPedidoDesc();

    // Pedidos en rango de fechas excluyendo cancelados
    @Query("SELECT p FROM Pedido p WHERE p.estado != 'CANCELADO' " +
           "AND p.fechaPedido BETWEEN :desde AND :hasta")
    List<Pedido> findPedidosActivosPorFecha(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    // Total acumulado de ventas
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado != 'CANCELADO'")
    java.math.BigDecimal sumTotalVentas();

    // Total acumulado por rango de fechas
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado != 'CANCELADO' " +
           "AND p.fechaPedido BETWEEN :desde AND :hasta")
    java.math.BigDecimal sumTotalVentasPorFecha(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    // Conteo de pedidos activos
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado != 'CANCELADO'")
    long countPedidosActivos();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado != 'CANCELADO' " +
           "AND p.fechaPedido BETWEEN :desde AND :hasta")
    long countPedidosActivosPorFecha(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}