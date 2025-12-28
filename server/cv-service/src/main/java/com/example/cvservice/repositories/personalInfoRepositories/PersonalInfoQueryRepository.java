package com.example.cvservice.repositories.personalInfoRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.PersonalInfo;

import java.util.Optional;
import java.util.UUID;

/**
 * PersonalInfoQueryRepository - Sử dụng @Query với JPQL để truy vấn
 */
@Repository
public interface PersonalInfoQueryRepository extends JpaRepository<PersonalInfo, UUID> {

    /**
     * Tìm personal info theo CV ID
     */
    @Query("SELECT p FROM PersonalInfo p WHERE p.cvId = :cvId")
    Optional<PersonalInfo> findByCvId(@Param("cvId") UUID cvId);

    /**
     * Tìm personal info theo email
     */
    @Query("SELECT p FROM PersonalInfo p WHERE p.email = :email")
    Optional<PersonalInfo> findByEmail(@Param("email") String email);

    /**
     * Kiểm tra personal info tồn tại theo CV ID
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PersonalInfo p WHERE p.cvId = :cvId")
    boolean existsByCvId(@Param("cvId") UUID cvId);

    /**
     * Kiểm tra email đã tồn tại
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PersonalInfo p WHERE p.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
