package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "plan")
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @NotBlank(message = "El nombre del plan es obligatorio.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El precio mensual es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0.")
    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "max_productos")
    private Integer maxProductos = 50;

    @Column(name = "max_colaboradores")
    private Integer maxColaboradores = 2;

    @Column(name = "incluye_reportes")
    private boolean incluyeReportes = false;

    @Column(name = "incluye_cupones")
    private boolean incluyeCupones = false;

    private boolean activo = true;
}
