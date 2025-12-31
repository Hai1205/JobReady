package com.example.userservice.repositories.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.Plan;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanCommandRepository extends JpaRepository<Plan, UUID> {

        @Modifying
        @Transactional
        @Query("DELETE FROM Plan p WHERE p.id = :planId")
        int deletePlanById(@Param("planId") UUID planId);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO plans (id, type, title, price, currency, period, description, features, is_recommended, is_popular) "
                        +
                        "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)", nativeQuery = true)
        int insertPlan(UUID id,
                        String type,
                        String title,
                        Long price,
                        String currency,
                        String period,
                        String description,
                        String features,
                        boolean isRecommended,
                        boolean isPopular);

        @Modifying
        @Transactional
        @Query("UPDATE Plan p SET p.type = :type, p.title = :title, p.price = :price, p.currency = :currency, " +
                        "p.period = :period, p.description = :description, p.features = :features, " +
                        "p.isRecommended = :isRecommended, p.isPopular = :isPopular WHERE p.id = :planId")
        int updatePlanAllFields(@Param("planId") UUID planId,
                        @Param("type") String type,
                        @Param("title") String title,
                        @Param("price") Long price,
                        @Param("currency") String currency,
                        @Param("period") String period,
                        @Param("description") String description,
                        @Param("features") List<String> features,
                        @Param("isRecommended") boolean isRecommended,
                        @Param("isPopular") boolean isPopular);
}
