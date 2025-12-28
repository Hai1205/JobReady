package com.example.cvservice.repositories.educationRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cvservice.entities.Education;

import java.util.UUID;

/**
 * EducationCommandRepository - Sử dụng @Modifying cho các operations UPDATE, DELETE
 */
@Repository
public interface EducationCommandRepository extends JpaRepository<Education, UUID> {

    /**
     * Insert Education mới
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO educations (id, cv_id, school, degree, field, start_date, end_date) " +
                   "VALUES (:id, :cvId, :school, :degree, :field, :startDate, :endDate)",
           nativeQuery = true)
    int insertEducation(@Param("id") UUID id,
                       @Param("cvId") UUID cvId,
                       @Param("school") String school,
                       @Param("degree") String degree,
                       @Param("field") String field,
                       @Param("startDate") String startDate,
                       @Param("endDate") String endDate);

    /**
     * Xóa tất cả educations của một CV
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Education e WHERE e.cvId = :cvId")
    int deleteAllByCvId(@Param("cvId") UUID cvId);

    /**
     * Xóa education theo ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Education e WHERE e.id = :educationId")
    int deleteEducationById(@Param("educationId") UUID educationId);
}
