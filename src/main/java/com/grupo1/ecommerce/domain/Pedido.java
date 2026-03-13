package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero_pedido", nullable = false, length = 20, unique = true)
    private String numeroPedido;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    @ManyToOne
    @JoinColumn(name = "id_zona_envio")
    private ZonaEnvio zonaEnvio;

    @NotBlank(message = "La dirección de envío es obligatoria.")
    @Size(max = 255)
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Size(max = 20)
    @Column(name = "telefono_envio", length = 20)
    private String telefonoEnvio;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
