package com.grupo1.ecommerce.service;

import com.grupo1.ecommerce.domain.Pedido;
import com.grupo1.ecommerce.domain.PedidoDetalle;
import com.grupo1.ecommerce.repository.PedidoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoDao;

    public List<Pedido> getPedidos(){
        return pedidoDao.findAllByOrderByFechaPedidoDesc();
    }

    public void save(Pedido pedido){

        BigDecimal subtotal = BigDecimal.ZERO;

        // CALCULAR SUBTOTAL DESDE DETALLES
        if (pedido.getDetalles() != null) {
            for (PedidoDetalle d : pedido.getDetalles()) {

                // subtotal por línea
                BigDecimal sub = d.getPrecio()
                        .multiply(BigDecimal.valueOf(d.getCantidad()));

                d.setSubtotal(sub);

                // acumular
                subtotal = subtotal.add(sub);

                // asignar relación
                d.setPedido(pedido);
            }
        }

        pedido.setSubtotal(subtotal);

        // COSTO ENVÍO DESDE ZONA
        if (pedido.getZonaEnvio() != null) {
            pedido.setCostoEnvio(
                BigDecimal.valueOf(pedido.getZonaEnvio().getCosto())
            );
        } else {
            pedido.setCostoEnvio(BigDecimal.ZERO);
        }

        // TOTAL FINAL
        pedido.setTotal(
            pedido.getSubtotal().add(pedido.getCostoEnvio())
        );

        pedidoDao.save(pedido);
    }
}