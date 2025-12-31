package com.example.userservice.repositories.plan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.Plan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanQueryRepository extends JpaRepository<Plan, UUID> {

    @Query("SELECT p FROM Plan p")
    Page<Plan> findAllPlans(Pageable pageable);

    @Query("SELECT p FROM Plan p")
    List<Plan> findAllPlansList();

    @Query("SELECT p FROM Plan p WHERE p.id = :planId")
    Optional<Plan> findPlanById(@Param("planId") UUID planId);

    @Query("SELECT p FROM Plan p WHERE p.type = :type")
    List<Plan> findPlansByType(@Param("type") String type);

    @Query("SELECT p FROM Plan p WHERE p.isRecommended = true")
    List<Plan> findRecommendedPlans();

    @Query("SELECT p FROM Plan p WHERE p.isPopular = true")
    List<Plan> findPopularPlans();

    @Query("SELECT p FROM Plan p WHERE p.name = :name")
    Optional<Plan> findByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Plan p WHERE p.name = :name")
    boolean existsByPlanName(@Param("name") String name);
}
