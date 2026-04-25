package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
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

    // =========================
    // RELACIONES
    // =========================

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

    @ManyToOne
    @JoinColumn(name = "id_cupon")
    private Cupon cupon;

    // DETALLE DEL PEDIDO (HU11)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoDetalle> detalles;

    // =========================
    // DATOS DE ENVÍO
    // =========================

    @NotBlank(message = "La dirección de envío es obligatoria.")
    @Size(max = 255)
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Size(max = 20)
    @Column(name = "telefono_envio", length = 20)
    private String telefonoEnvio;

    // =========================
    // MONTOS
    // =========================

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // =========================
    // ESTADO Y FECHAS
    // =========================

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // =========================
    // MÉTODOS AUTOMÁTICOS
    // =========================

    @PrePersist
    public void prePersist() {
        this.fechaPedido = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}