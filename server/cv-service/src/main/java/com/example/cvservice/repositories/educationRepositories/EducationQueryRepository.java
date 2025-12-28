package com.example.cvservice.repositories.educationRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.Education;

import java.util.List;
import java.util.UUID;

/**
 * EducationQueryRepository - Sử dụng @Query với JPQL để truy vấn
 */
@Repository
public interface EducationQueryRepository extends JpaRepository<Education, UUID> {

    /**
     * Lấy tất cả educations của một CV
     */
    @Query("SELECT e FROM Education e WHERE e.cvId = :cvId ORDER BY e.startDate DESC")
    List<Education> findByCvId(@Param("cvId") UUID cvId);

    /**
     * Đếm số educations của một CV
     */
    @Query("SELECT COUNT(e) FROM Education e WHERE e.cvId = :cvId")
    long countByCvId(@Param("cvId") UUID cvId);

    /**
     * Kiểm tra CV có education nào không
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Education e WHERE e.cvId = :cvId")
    boolean existsByCvId(@Param("cvId") UUID cvId);
}
