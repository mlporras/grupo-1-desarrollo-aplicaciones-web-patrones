package com.grupo1.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Colaborador;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.Usuario;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Integer> {

    List<Colaborador> findByTienda(Tienda tienda);

    List<Colaborador> findByTiendaAndActivoTrue(Tienda tienda);

    Optional<Colaborador> findByUsuarioAndTienda(Usuario usuario, Tienda tienda);

    boolean existsByUsuarioAndTienda(Usuario usuario, Tienda tienda);

    Optional<Colaborador> findByUsuarioAndActivoTrue(Usuario usuario);
}
