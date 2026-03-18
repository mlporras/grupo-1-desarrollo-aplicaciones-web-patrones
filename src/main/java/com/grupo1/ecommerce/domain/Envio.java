package com.grupo1.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String direccion;

    private double costo;

    private boolean activo;
}