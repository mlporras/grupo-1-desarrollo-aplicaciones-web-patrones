package com.grupo1.ecommerce.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Plan;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.repository.PlanRepository;
import com.grupo1.ecommerce.repository.TiendaRepository;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final TiendaRepository tiendaRepository;

    public PlanService(PlanRepository planRepository, TiendaRepository tiendaRepository) {
        this.planRepository = planRepository;
        this.tiendaRepository = tiendaRepository;
    }

    @Transactional(readOnly = true)
    public List<Plan> getPlanes() {
        return planRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Plan> getPlanesActivos() {
        return planRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Plan> getPlan(Integer id) {
        return planRepository.findById(id);
    }

    @Transactional
    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    @Transactional
    public Tienda asignarPlan(Integer idTienda, Integer idPlan) {
        Tienda tienda = tiendaRepository.findById(idTienda)
                .orElseThrow(() -> new IllegalArgumentException("Tienda no encontrada."));
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado."));

        tienda.setPlan(plan);
        tienda.setEstadoSuscripcion("ACTIVA");
        tienda.setFechaVencimiento(LocalDate.now().plusDays(30));
        return tiendaRepository.save(tienda);
    }

    @Transactional
    public Tienda cambiarPlan(Integer idTienda, Integer idPlan) {
        return asignarPlan(idTienda, idPlan);
    }

    @Transactional
    public void verificarSuspension(Tienda tienda) {
        if (tienda.getFechaVencimiento() != null
                && LocalDate.now().isAfter(tienda.getFechaVencimiento())
                && !"SUSPENDIDA".equals(tienda.getEstadoSuscripcion())) {
            tienda.setEstadoSuscripcion("SUSPENDIDA");
            tiendaRepository.save(tienda);
        }
    }

    @Transactional
    public Tienda reactivar(Integer idTienda) {
        Tienda tienda = tiendaRepository.findById(idTienda)
                .orElseThrow(() -> new IllegalArgumentException("Tienda no encontrada."));
        tienda.setEstadoSuscripcion("ACTIVA");
        tienda.setFechaVencimiento(LocalDate.now().plusDays(30));
        return tiendaRepository.save(tienda);
    }
}
