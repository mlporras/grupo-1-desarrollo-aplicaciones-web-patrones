package com.grupo1.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.CarritoItem;
import com.grupo1.ecommerce.domain.Producto;
import com.grupo1.ecommerce.domain.Usuario;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {

    List<CarritoItem> findByUsuario(Usuario usuario);

    Optional<CarritoItem> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    void deleteByUsuario(Usuario usuario);
}
