package com.example.aiservice.repositories;

import com.example.aiservice.entities.CVTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CVTemplateQueryRepository extends JpaRepository<CVTemplateEntity, String> {

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.id = :id")
    Optional<CVTemplateEntity> findTemplateById(@Param("id") String id);

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.isActive = true")
    List<CVTemplateEntity> findByIsActiveTrue();

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.category = :category AND t.isActive = true")
    List<CVTemplateEntity> findByCategoryAndIsActiveTrue(@Param("category") String category);

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.section = :section AND t.isActive = true")
    List<CVTemplateEntity> findBySectionAndIsActiveTrue(@Param("section") String section);

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.category = :category AND t.level = :level AND t.isActive = true")
    List<CVTemplateEntity> findByCategoryAndLevelAndIsActiveTrue(
            @Param("category") String category,
            @Param("level") String level);

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.rating >= :minRating AND t.isActive = true")
    List<CVTemplateEntity> findByRatingGreaterThanEqualAndIsActiveTrue(@Param("minRating") Integer minRating);

    @Query("SELECT t FROM CVTemplateEntity t WHERE t.category = :category AND t.level = :level AND t.section = :section AND t.rating >= :minRating AND t.isActive = true")
    List<CVTemplateEntity> findByCategoryLevelSectionAndMinRating(
            @Param("category") String category,
            @Param("level") String level,
            @Param("section") String section,
            @Param("minRating") Integer minRating);

    @Query("SELECT COUNT(t) FROM CVTemplateEntity t")
    long countAllTemplates();
}
