package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.DetallePedido;
import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.DetallePedidoRepository;
import com.grupo1.ecommerce.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoService carritoService;
    private final InventarioService inventarioService;
    private final MetodoPagoService metodoPagoService; // Corregido: Se añadió la declaración

    // Corregido: El constructor ahora incluye todos los servicios necesarios
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
        // 1. Obtener items del carrito
        List<CarritoItem> items = carritoService.getItemsPorUsuario(usuario);
        if (items.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        // 2. Crear y guardar la cabecera del Pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNumeroPedido(generarNumeroPedido());
        
        // Corregido: Una sola asignación con el tipo LocalDateTime correcto
        pedido.setFechaPedido(LocalDateTime.now());
        
        pedido.setDireccionEnvio(direccion);
        
        // Corregido: Ahora metodoPagoService está disponible para buscar el objeto
        metodoPagoService.getMetodoPorNombre(metodoPago)
                         .ifPresent(pedido::setMetodoPago);
                         
        pedido.setEstado("PAGADO");
        
        // Corregido: Conversión segura de double a BigDecimal
        pedido.setTotal(BigDecimal.valueOf(carritoService.calcularTotal(usuario)));
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 3. Procesar cada item: Crear detalle y descontar stock
        for (CarritoItem item : items) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getProducto().getPrecio());
            detallePedidoRepository.save(detalle);

            // Descontar del inventario físicamente
            inventarioService.descontarStock(item.getProducto(), item.getCantidad());
        }

        // 4. Limpiar el carrito del usuario tras el éxito
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
}