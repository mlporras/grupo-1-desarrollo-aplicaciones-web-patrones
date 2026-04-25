package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "colaborador",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_tienda"}))
public class Colaborador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colaborador")
    private Integer idColaborador;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tienda", nullable = false)
    private Tienda tienda;

    @Column(name = "rol_colaborador", nullable = false, length = 20)
    private String rolColaborador = "VIEWER";

    private boolean activo = true;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @PrePersist
    public void prePersist() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}
