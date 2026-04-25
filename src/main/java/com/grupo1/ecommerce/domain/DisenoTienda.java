package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "diseno_tienda")
public class DisenoTienda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diseno")
    private Integer idDiseno;

    @ManyToOne
    @JoinColumn(name = "id_tienda", nullable = false)
    private Tienda tienda;

    @Size(max = 50)
    @Column(length = 50)
    private String plantilla = "default";

    @Size(max = 7)
    @Column(name = "color_primario", length = 7)
    private String colorPrimario = "#1a1a2e";

    @Size(max = 7)
    @Column(name = "color_secundario", length = 7)
    private String colorSecundario = "#0f3460";

    @Size(max = 7)
    @Column(name = "color_acento", length = 7)
    private String colorAcento = "#e2a03f";

    @Size(max = 1024)
    @Column(name = "ruta_logo", length = 1024)
    private String rutaLogo;

    private boolean borrador = true;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
