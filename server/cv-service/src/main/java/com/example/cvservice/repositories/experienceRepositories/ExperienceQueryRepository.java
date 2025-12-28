package com.example.cvservice.repositories.experienceRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.Experience;

import java.util.List;
import java.util.UUID;

/**
 * ExperienceQueryRepository - Sử dụng @Query với JPQL để truy vấn
 */
@Repository
public interface ExperienceQueryRepository extends JpaRepository<Experience, UUID> {

    /**
     * Lấy tất cả experiences của một CV
     */
    @Query("SELECT e FROM Experience e WHERE e.cvId = :cvId ORDER BY e.startDate DESC")
    List<Experience> findByCvId(@Param("cvId") UUID cvId);

    /**
     * Đếm số experiences của một CV
     */
    @Query("SELECT COUNT(e) FROM Experience e WHERE e.cvId = :cvId")
    long countByCvId(@Param("cvId") UUID cvId);

    /**
     * Kiểm tra CV có experience nào không
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Experience e WHERE e.cvId = :cvId")
    boolean existsByCvId(@Param("cvId") UUID cvId);
}
