package com.grupo1.ecommerce.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.grupo1.ecommerce.domain.DisenoTienda;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.repository.DisenoTiendaRepository;

@Service
public class DisenoTiendaService {

    private final DisenoTiendaRepository disenoRepository;

    private static final String LOGOS_DIR = "target/classes/static/img/logos";

    public DisenoTiendaService(DisenoTiendaRepository disenoRepository) {
        this.disenoRepository = disenoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<DisenoTienda> getPublicado(Tienda tienda) {
        return disenoRepository.findByTiendaAndBorradorFalse(tienda);
    }

    @Transactional(readOnly = true)
    public Optional<DisenoTienda> getBorrador(Tienda tienda) {
        return disenoRepository.findByTiendaAndBorradorTrue(tienda);
    }

    @Transactional
    public DisenoTienda guardarBorrador(DisenoTienda diseno, MultipartFile logoFile) {
        diseno.setBorrador(true);

        Optional<DisenoTienda> existente = disenoRepository
                .findByTiendaAndBorradorTrue(diseno.getTienda());
        if (existente.isPresent()) {
            DisenoTienda actual = existente.get();
            actual.setPlantilla(diseno.getPlantilla());
            actual.setColorPrimario(diseno.getColorPrimario());
            actual.setColorSecundario(diseno.getColorSecundario());
            actual.setColorAcento(diseno.getColorAcento());
            diseno = actual;
        }

        diseno = disenoRepository.save(diseno);

        if (logoFile != null && !logoFile.isEmpty()) {
            String rutaLogo = guardarLogo(logoFile, diseno.getTienda().getIdTienda());
            diseno.setRutaLogo(rutaLogo);
            diseno = disenoRepository.save(diseno);
        }

        return diseno;
    }

    @Transactional
    public DisenoTienda publicar(Tienda tienda) {
        DisenoTienda borrador = disenoRepository.findByTiendaAndBorradorTrue(tienda)
                .orElseThrow(() -> new IllegalStateException("No hay borrador para publicar."));

        Optional<DisenoTienda> publicadoAnterior = disenoRepository
                .findByTiendaAndBorradorFalse(tienda);
        publicadoAnterior.ifPresent(disenoRepository::delete);

        borrador.setBorrador(false);
        return disenoRepository.save(borrador);
    }

    @Transactional
    public void revertir(Tienda tienda) {
        Optional<DisenoTienda> borrador = disenoRepository
                .findByTiendaAndBorradorTrue(tienda);
        borrador.ifPresent(disenoRepository::delete);
    }

    private String guardarLogo(MultipartFile file, Integer idTienda) {
        try {
            Path dir = Paths.get(LOGOS_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = "logo_tienda_" + idTienda + extension;
            Path destino = dir.resolve(filename);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return "/img/logos/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el logo.", e);
        }
    }
}
