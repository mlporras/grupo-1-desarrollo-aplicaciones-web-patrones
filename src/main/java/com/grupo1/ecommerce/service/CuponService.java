package com.grupo1.ecommerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Cupon;
import com.grupo1.ecommerce.repository.CuponRepository;

@Service
public class CuponService {

    private final CuponRepository cuponRepository;

    public CuponService(CuponRepository cuponRepository) {
        this.cuponRepository = cuponRepository;
    }

    @Transactional(readOnly = true)
    public List<Cupon> getCupones() {
        return cuponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cupon> getCupon(Integer id) {
        return cuponRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cupon> getCuponPorCodigo(String codigo) {
        return cuponRepository.findByCodigo(codigo);
    }

    @Transactional
    public Cupon save(Cupon cupon) {
        if (cupon.getIdCupon() == null && cuponRepository.existsByCodigo(cupon.getCodigo())) {
            throw new IllegalArgumentException("El código de cupón ya existe.");
        }
        return cuponRepository.save(cupon);
    }

    @Transactional
    public void desactivar(Integer id) {
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado."));
        cupon.setActivo(false);
        cuponRepository.save(cupon);
    }

    public Optional<Cupon> validarCupon(String codigo) {
        Optional<Cupon> cuponOpt = cuponRepository.findByCodigo(codigo);
        if (cuponOpt.isEmpty()) return Optional.empty();

        Cupon cupon = cuponOpt.get();
        LocalDate hoy = LocalDate.now();

        if (!cupon.isActivo()) return Optional.empty();
        if (hoy.isBefore(cupon.getFechaInicio()) || hoy.isAfter(cupon.getFechaFin())) return Optional.empty();
        if (cupon.getUsosMaximos() > 0 && cupon.getUsosActuales() >= cupon.getUsosMaximos()) return Optional.empty();

        return Optional.of(cupon);
    }

    public BigDecimal calcularDescuento(Cupon cupon, BigDecimal subtotal) {
        if ("PORCENTAJE".equals(cupon.getTipoDescuento())) {
            return subtotal.multiply(cupon.getValor())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            return cupon.getValor().min(subtotal);
        }
    }

    @Transactional
    public void incrementarUso(Cupon cupon) {
        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        cuponRepository.save(cupon);
    }
}
