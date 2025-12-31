package com.example.userservice.repositories.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.Plan;

import java.util.UUID;

@Repository
public interface SimplePlanRepository extends JpaRepository<Plan, UUID> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Plan p WHERE p.id = :planId")
    boolean existsByPlanId(@Param("planId") UUID planId);
}
