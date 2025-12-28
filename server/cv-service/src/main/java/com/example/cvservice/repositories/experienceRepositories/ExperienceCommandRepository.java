package com.example.cvservice.repositories.experienceRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cvservice.entities.Experience;

import java.util.UUID;

/**
 * ExperienceCommandRepository - Sử dụng @Modifying cho các operations UPDATE, DELETE
 */
@Repository
public interface ExperienceCommandRepository extends JpaRepository<Experience, UUID> {

    /**
     * Insert Experience mới
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO experiences (id, cv_id, company, position, start_date, end_date, description) " +
                   "VALUES (:id, :cvId, :company, :position, :startDate, :endDate, :description)",
           nativeQuery = true)
    int insertExperience(@Param("id") UUID id,
                        @Param("cvId") UUID cvId,
                        @Param("company") String company,
                        @Param("position") String position,
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate,
                        @Param("description") String description);

    /**
     * Xóa tất cả experiences của một CV
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Experience e WHERE e.cvId = :cvId")
    int deleteAllByCvId(@Param("cvId") UUID cvId);

    /**
     * Xóa experience theo ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Experience e WHERE e.id = :experienceId")
    int deleteExperienceById(@Param("experienceId") UUID experienceId);
}
