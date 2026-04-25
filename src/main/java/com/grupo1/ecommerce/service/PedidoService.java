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

import com.grupo1.ecommerce.domain.Cupon;
import com.grupo1.ecommerce.domain.ZonaEnvio;

import java.math.BigDecimal;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoService carritoService;
    private final InventarioService inventarioService;
    private final MetodoPagoService metodoPagoService;
    private final CuponService cuponService;

    public PedidoService(PedidoRepository pedidoRepository,
                         DetallePedidoRepository detallePedidoRepository,
                         CarritoService carritoService,
                         InventarioService inventarioService,
                         MetodoPagoService metodoPagoService,
                         CuponService cuponService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.carritoService = carritoService;
        this.inventarioService = inventarioService;
        this.metodoPagoService = metodoPagoService;
        this.cuponService = cuponService;
    }

    @Transactional
    public Pedido procesarCompra(Usuario usuario, String direccion, String metodoPago) {
        return procesarCompra(usuario, direccion, metodoPago, null);
    }

    @Transactional
    public Pedido procesarCompra(Usuario usuario, String direccion, String metodoPago, Cupon cupon) {
        return procesarCompra(usuario, direccion, metodoPago, cupon, null);
    }

    @Transactional
    public Pedido procesarCompra(Usuario usuario, String direccion, String metodoPago,
                                 Cupon cupon, ZonaEnvio zonaEnvio) {
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

        // Debe coincidir con el ENUM de BD: PENDIENTE/CONFIRMADO/ENVIADO/ENTREGADO/CANCELADO
        pedido.setEstado("CONFIRMADO");

        if (zonaEnvio != null) {
            pedido.setZonaEnvio(zonaEnvio);
            pedido.setCostoEnvio(zonaEnvio.getCostoEnvio());
        } else {
            pedido.setCostoEnvio(BigDecimal.ZERO);
        }

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

        BigDecimal descuento = BigDecimal.ZERO;
        if (cupon != null) {
            descuento = cuponService.calcularDescuento(cupon, subtotal);
            pedidoGuardado.setCupon(cupon);
            pedidoGuardado.setDescuento(descuento);
            cuponService.incrementarUso(cupon);
        }

        pedidoGuardado.setTotal(subtotal.add(pedidoGuardado.getCostoEnvio()).subtract(descuento));
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

        pedido.setTotal(
            pedido.getSubtotal().add(pedido.getCostoEnvio())
        );

        pedidoRepository.save(pedido);
    }
}
