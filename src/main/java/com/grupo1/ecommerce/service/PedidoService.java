package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.DetallePedido;
import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.DetallePedidoRepository;
import com.grupo1.ecommerce.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> getTodosPedidos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> getPedido(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> getPedidoPorNumero(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> getDetallesPedido(Pedido pedido) {
        return detallePedidoRepository.findByPedido(pedido);
    }

    @Transactional
    public void savePedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void saveDetalle(DetallePedido detalle) {
        detallePedidoRepository.save(detalle);
    }

    public String generarNumeroPedido() {
        long count = pedidoRepository.count() + 1;
        return String.format("PED-%06d", count);
    }
}
