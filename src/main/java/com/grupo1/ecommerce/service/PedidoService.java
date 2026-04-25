package com.grupo1.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.PedidoDetalle;
import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.DetallePedidoRepository;
import com.grupo1.ecommerce.repository.PedidoRepository;

import java.math.BigDecimal;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoService carritoService;
    private final InventarioService inventarioService;
    private final MetodoPagoService metodoPagoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         DetallePedidoRepository detallePedidoRepository,
                         CarritoService carritoService,
                         InventarioService inventarioService,
                         MetodoPagoService metodoPagoService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.carritoService = carritoService;
        this.inventarioService = inventarioService;
        this.metodoPagoService = metodoPagoService;
    }

    @Transactional
    public Pedido procesarCompra(Usuario usuario, String direccion, String metodoPago) {
        List<CarritoItem> items = carritoService.getItemsPorUsuario(usuario);
        if (items.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setDireccionEnvio(direccion);

        metodoPagoService.getMetodoPorNombre(metodoPago)
                         .ifPresent(pedido::setMetodoPago);

        pedido.setEstado("PAGADO");
        pedido.setCostoEnvio(BigDecimal.ZERO);

        pedido.setSubtotal(BigDecimal.ZERO);
        pedido.setTotal(BigDecimal.ZERO);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (CarritoItem item : items) {
            BigDecimal precio = item.getProducto().getPrecio();
            BigDecimal sub = precio.multiply(BigDecimal.valueOf(item.getCantidad()));

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(precio);
            detalle.setSubtotal(sub);
            detallePedidoRepository.save(detalle);

            subtotal = subtotal.add(sub);

            inventarioService.descontarStock(item.getProducto(), item.getCantidad());
        }

        pedidoGuardado.setSubtotal(subtotal);
        pedidoGuardado.setTotal(subtotal.add(pedidoGuardado.getCostoEnvio()));
        pedidoRepository.save(pedidoGuardado);

        carritoService.vaciarCarrito(usuario);

        return pedidoGuardado;
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
    public Optional<Pedido> getPedido(Integer id) {
        return pedidoRepository.findById(id);
    }

    public String generarNumeroPedido() {
        long count = pedidoRepository.count() + 1;
        return String.format("PED-%06d", count);
    }

    public void save(Pedido pedido) {
        BigDecimal subtotal = BigDecimal.ZERO;

        if (pedido.getDetalles() != null) {
            for (PedidoDetalle d : pedido.getDetalles()) {
                BigDecimal sub = d.getPrecio()
                        .multiply(BigDecimal.valueOf(d.getCantidad()));
                d.setSubtotal(sub);
                subtotal = subtotal.add(sub);
                d.setPedido(pedido);
            }
        }

        pedido.setSubtotal(subtotal);

        if (pedido.getZonaEnvio() != null) {
            pedido.setCostoEnvio(pedido.getZonaEnvio().getCostoEnvio());
        } else {
            pedido.setCostoEnvio(BigDecimal.ZERO);
        }

        pedido.setTotal(pedido.getSubtotal().add(pedido.getCostoEnvio()));
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void cambiarEstado(Integer idPedido, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        List<String> estadosPermitidos = List.of("ENVIADO", "ENTREGADO");
        if (!estadosPermitidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no permitido: " + nuevoEstado);
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if ("CANCELADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya está cancelado");
        }

        if (pedido.getDetalles() != null) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                inventarioService.actualizarStock(
                    detalle.getProducto(),
                    inventarioService.getStock(detalle.getProducto()) + detalle.getCantidad()
                );
            }
        }

        pedido.setEstado("CANCELADO");
        pedidoRepository.save(pedido);
    }
}