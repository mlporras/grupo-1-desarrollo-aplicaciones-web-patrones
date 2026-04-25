package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cupon")
public class Cupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cupon")
    private Integer idCupon;

    @NotBlank(message = "El código del cupón es obligatorio.")
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String codigo;

    @NotBlank(message = "El tipo de descuento es obligatorio.")
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    private String tipoDescuento;

    @NotNull(message = "El valor del descuento es obligatorio.")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria.")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "usos_maximos")
    private Integer usosMaximos = 0;

    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;

    private boolean activo = true;
}
