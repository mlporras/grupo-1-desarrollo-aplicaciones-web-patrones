package com.grupo1.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.PedidoDetalle;
import com.grupo1.ecommerce.domain.Pedido;

public interface DetallePedidoRepository extends JpaRepository<PedidoDetalle, Integer> {

    List<PedidoDetalle> findByPedido(Pedido pedido);
}
