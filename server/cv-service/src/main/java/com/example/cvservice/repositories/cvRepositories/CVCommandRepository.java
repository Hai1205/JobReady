package com.example.cvservice.repositories.cvRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cvservice.entities.CV;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CVCommandRepository - Sử dụng @Modifying cho các operations CREATE, UPDATE, DELETE
 * Tất cả các operations ghi (write) sẽ được thực hiện qua repository này
 */
@Repository
public interface CVCommandRepository extends JpaRepository<CV, UUID> {

    /**
     * Cập nhật title của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.title = :title, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVTitle(@Param("cvId") UUID cvId, 
                      @Param("title") String title,
                      @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật visibility của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.isVisibility = :isVisibility, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVVisibility(@Param("cvId") UUID cvId, 
                           @Param("isVisibility") boolean isVisibility,
                           @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật skills của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.skills = :skills, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVSkills(@Param("cvId") UUID cvId, 
                       @Param("skills") List<String> skills,
                       @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật theme (color, template, font) của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.color = :color, c.template = :template, c.font = :font, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVTheme(@Param("cvId") UUID cvId,
                      @Param("color") String color,
                      @Param("template") String template,
                      @Param("font") String font,
                      @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật color của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.color = :color, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVColor(@Param("cvId") UUID cvId,
                      @Param("color") String color,
                      @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật template của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.template = :template, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVTemplate(@Param("cvId") UUID cvId,
                         @Param("template") String template,
                         @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật font của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.font = :font, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVFont(@Param("cvId") UUID cvId,
                     @Param("font") String font,
                     @Param("updatedAt") Instant updatedAt);

    /**
     * Insert CV mới
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO cvs (id, user_id, title, skills, is_visibility, color, template, font, created_at, updated_at) " +
                   "VALUES (:id, :userId, :title, :skills, :isVisibility, :color, :template, :font, :createdAt, :updatedAt)", 
           nativeQuery = true)
    int insertCV(@Param("id") UUID id,
                 @Param("userId") UUID userId,
                 @Param("title") String title,
                 @Param("skills") String skills,
                 @Param("isVisibility") boolean isVisibility,
                 @Param("color") String color,
                 @Param("template") String template,
                 @Param("font") String font,
                 @Param("createdAt") Instant createdAt,
                 @Param("updatedAt") Instant updatedAt);

    /**
     * Cập nhật updatedAt của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVUpdatedAt(@Param("cvId") UUID cvId, @Param("updatedAt") Instant updatedAt);

    /**
     * Xóa CV theo ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CV c WHERE c.id = :cvId")
    int deleteCVById(@Param("cvId") UUID cvId);

    /**
     * Xóa tất cả CVs của user
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CV c WHERE c.userId = :userId")
    int deleteAllCVsByUserId(@Param("userId") UUID userId);

    /**
     * Cập nhật toàn bộ thông tin cơ bản của CV
     */
    @Modifying
    @Transactional
    @Query("UPDATE CV c SET c.title = :title, c.skills = :skills, c.isVisibility = :isVisibility, " +
           "c.color = :color, c.template = :template, c.font = :font, c.updatedAt = :updatedAt WHERE c.id = :cvId")
    int updateCVBasicInfo(@Param("cvId") UUID cvId,
                          @Param("title") String title,
                          @Param("skills") List<String> skills,
                          @Param("isVisibility") boolean isVisibility,
                          @Param("color") String color,
                          @Param("template") String template,
                          @Param("font") String font,
                          @Param("updatedAt") Instant updatedAt);
}
