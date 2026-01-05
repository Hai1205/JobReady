package com.example.aiservice.repositories;

import com.example.aiservice.entities.CVTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CVTemplateCommandRepository extends JpaRepository<CVTemplateEntity, String> {

    @Modifying
    @Query(value = "INSERT INTO cv_templates (id, category, level, section, content, rating, keywords, is_active, created_at, updated_at) "
            +
            "VALUES (:id, :category, :level, :section, :content, :rating, :keywords, :isActive, :createdAt, :updatedAt)", nativeQuery = true)
    void insertTemplate(
            @Param("id") String id,
            @Param("category") String category,
            @Param("level") String level,
            @Param("section") String section,
            @Param("content") String content,
            @Param("rating") Integer rating,
            @Param("keywords") String keywords,
            @Param("isActive") Boolean isActive,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE CVTemplateEntity t SET " +
            "t.content = :content, " +
            "t.category = :category, " +
            "t.level = :level, " +
            "t.section = :section, " +
            "t.rating = :rating, " +
            "t.keywords = :keywords, " +
            "t.updatedAt = :updatedAt " +
            "WHERE t.id = :id")
    int updateTemplate(
            @Param("id") String id,
            @Param("content") String content,
            @Param("category") String category,
            @Param("level") String level,
            @Param("section") String section,
            @Param("rating") Integer rating,
            @Param("keywords") List<String> keywords,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE CVTemplateEntity t SET t.isActive = false, t.updatedAt = :updatedAt WHERE t.id = :id")
    int softDeleteTemplate(@Param("id") String id, @Param("updatedAt") LocalDateTime updatedAt);
}
