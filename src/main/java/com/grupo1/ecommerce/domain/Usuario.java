package com.grupo1.ecommerce.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo no puede estar vacío.")
    @Size(max = 150)
    @Column(nullable = false, length = 150, unique = true)
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener al menos 8 caracteres.")
    @Column(nullable = false)
    private String contrasena;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Size(max = 255)
    private String direccion;

    @Column(nullable = false)
    private String rol = "CLIENTE";

    private boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
