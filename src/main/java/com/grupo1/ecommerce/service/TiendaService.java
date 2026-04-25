package com.grupo1.ecommerce.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.TiendaConfigBackup;
import com.grupo1.ecommerce.repository.TiendaConfigBackupRepository;
import com.grupo1.ecommerce.repository.TiendaRepository;

@Service
public class TiendaService {

    private final TiendaRepository tiendaRepository;
    private final TiendaConfigBackupRepository backupRepository;

    public TiendaService(TiendaRepository tiendaRepository,
                         TiendaConfigBackupRepository backupRepository) {
        this.tiendaRepository = tiendaRepository;
        this.backupRepository = backupRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Tienda> getTienda(Integer id) {
        return tiendaRepository.findById(id);
    }

    @Transactional
    public Tienda guardarConfiguracion(Tienda tienda) {
        Tienda existente = tiendaRepository.findById(tienda.getIdTienda())
                .orElseThrow(() -> new IllegalArgumentException("Tienda no encontrada."));

        TiendaConfigBackup backup = new TiendaConfigBackup();
        backup.setTienda(existente);
        backup.setNombreComercial(existente.getNombreComercial());
        backup.setDescripcion(existente.getDescripcion());
        backup.setCorreoContacto(existente.getCorreoContacto());
        backup.setTelefonoContacto(existente.getTelefonoContacto());
        backup.setMoneda(existente.getMoneda());
        backupRepository.save(backup);

        existente.setNombreComercial(tienda.getNombreComercial());
        existente.setDescripcion(tienda.getDescripcion());
        existente.setCorreoContacto(tienda.getCorreoContacto());
        existente.setTelefonoContacto(tienda.getTelefonoContacto());
        existente.setMoneda(tienda.getMoneda());

        return tiendaRepository.save(existente);
    }

    @Transactional
    public Tienda restaurarConfiguracion(Integer idTienda) {
        Tienda tienda = tiendaRepository.findById(idTienda)
                .orElseThrow(() -> new IllegalArgumentException("Tienda no encontrada."));

        TiendaConfigBackup backup = backupRepository
                .findTopByTiendaOrderByFechaBackupDesc(tienda)
                .orElseThrow(() -> new IllegalStateException("No hay configuración anterior para restaurar."));

        tienda.setNombreComercial(backup.getNombreComercial());
        tienda.setDescripcion(backup.getDescripcion());
        tienda.setCorreoContacto(backup.getCorreoContacto());
        tienda.setTelefonoContacto(backup.getTelefonoContacto());
        tienda.setMoneda(backup.getMoneda());

        return tiendaRepository.save(tienda);
    }

    @Transactional
    public Tienda save(Tienda tienda) {
        return tiendaRepository.save(tienda);
    }
}
