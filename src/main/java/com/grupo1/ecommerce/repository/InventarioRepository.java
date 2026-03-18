package com.grupo1.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.grupo1.ecommerce.domain.Inventario;
import com.grupo1.ecommerce.domain.Producto;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByProducto(Producto producto);

    @Query("SELECT i FROM Inventario i WHERE i.stock <= i.umbralMinimo")
    List<Inventario> findStockBajo();
}
