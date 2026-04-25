package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "tienda")
public class Tienda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tienda")
    private Integer idTienda;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "El nombre comercial no puede estar vacío.")
    @Size(max = 150)
    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 150)
    @Column(name = "correo_contacto", length = 150)
    private String correoContacto;

    @Size(max = 20)
    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Size(max = 10)
    @Column(length = 10)
    private String moneda = "CRC";

    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @Column(name = "estado_suscripcion", length = 20)
    private String estadoSuscripcion = "PRUEBA";

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}
