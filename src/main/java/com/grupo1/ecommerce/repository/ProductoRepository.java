package com.grupo1.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Categoria;
import com.grupo1.ecommerce.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByCategoriaAndActivoTrue(Categoria categoria);
}
