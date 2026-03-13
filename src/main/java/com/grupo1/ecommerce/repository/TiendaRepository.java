package com.grupo1.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.Usuario;

public interface TiendaRepository extends JpaRepository<Tienda, Integer> {

    Optional<Tienda> findByUsuario(Usuario usuario);
}
