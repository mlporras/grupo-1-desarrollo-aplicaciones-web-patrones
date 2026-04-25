package com.grupo1.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.TiendaConfigBackup;

public interface TiendaConfigBackupRepository extends JpaRepository<TiendaConfigBackup, Integer> {

    Optional<TiendaConfigBackup> findTopByTiendaOrderByFechaBackupDesc(Tienda tienda);
}
