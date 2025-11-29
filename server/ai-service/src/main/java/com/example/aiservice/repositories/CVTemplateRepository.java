package com.example.aiservice.repositories;

import com.example.aiservice.entities.CVTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVTemplateRepository extends JpaRepository<CVTemplateEntity, String> {
    
    List<CVTemplateEntity> findByIsActiveTrue();
    
    List<CVTemplateEntity> findByCategoryAndIsActiveTrue(String category);
    
    List<CVTemplateEntity> findBySectionAndIsActiveTrue(String section);
    
    List<CVTemplateEntity> findByCategoryAndLevelAndIsActiveTrue(String category, String level);
    
    List<CVTemplateEntity> findByRatingGreaterThanEqualAndIsActiveTrue(Integer minRating);
    
    @Query("SELECT t FROM CVTemplateEntity t WHERE t.category = ?1 AND t.level = ?2 AND t.section = ?3 AND t.rating >= ?4 AND t.isActive = true")
    List<CVTemplateEntity> findByCategoryLevelSectionAndMinRating(
        String category, String level, String section, Integer minRating);
}