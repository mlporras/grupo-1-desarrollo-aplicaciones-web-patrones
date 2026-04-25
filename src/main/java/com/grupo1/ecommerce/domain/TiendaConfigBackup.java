package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tienda_config_backup")
public class TiendaConfigBackup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_backup")
    private Integer idBackup;

    @ManyToOne
    @JoinColumn(name = "id_tienda", nullable = false)
    private Tienda tienda;

    @Column(name = "nombre_comercial", length = 150)
    private String nombreComercial;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "correo_contacto", length = 150)
    private String correoContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(length = 10)
    private String moneda;

    @Column(name = "fecha_backup")
    private LocalDateTime fechaBackup;

    @PrePersist
    public void prePersist() {
        this.fechaBackup = LocalDateTime.now();
    }
}
