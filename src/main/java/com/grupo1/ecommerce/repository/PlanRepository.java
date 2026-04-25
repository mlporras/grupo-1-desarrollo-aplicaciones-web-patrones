package com.grupo1.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo1.ecommerce.domain.Plan;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    List<Plan> findByActivoTrue();
}
