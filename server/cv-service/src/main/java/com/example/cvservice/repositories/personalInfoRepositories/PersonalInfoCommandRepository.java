package com.example.cvservice.repositories.personalInfoRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cvservice.entities.PersonalInfo;

import java.util.UUID;

/**
 * PersonalInfoCommandRepository - Sử dụng @Modifying cho các operations UPDATE, DELETE
 */
@Repository
public interface PersonalInfoCommandRepository extends JpaRepository<PersonalInfo, UUID> {

    /**
     * Insert PersonalInfo mới
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO \"personal-infos\" (id, cv_id, fullname, email, phone, location, birth, summary, avatar_url, avatar_public_id) " +
                   "VALUES (:id, :cvId, :fullname, :email, :phone, :location, :birth, :summary, :avatarUrl, :avatarPublicId)",
           nativeQuery = true)
    int insertPersonalInfo(@Param("id") UUID id,
                          @Param("cvId") UUID cvId,
                          @Param("fullname") String fullname,
                          @Param("email") String email,
                          @Param("phone") String phone,
                          @Param("location") String location,
                          @Param("birth") String birth,
                          @Param("summary") String summary,
                          @Param("avatarUrl") String avatarUrl,
                          @Param("avatarPublicId") String avatarPublicId);

    /**
     * Xóa personal info theo CV ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PersonalInfo p WHERE p.cvId = :cvId")
    int deleteByCvId(@Param("cvId") UUID cvId);

    /**
     * Xóa personal info theo ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PersonalInfo p WHERE p.id = :personalInfoId")
    int deletePersonalInfoById(@Param("personalInfoId") UUID personalInfoId);

    /**
     * Cập nhật thông tin cơ bản của personal info
     */
    @Modifying
    @Transactional
    @Query("UPDATE PersonalInfo p SET p.fullname = :fullname, p.email = :email, p.phone = :phone, " +
           "p.location = :location, p.birth = :birth, p.summary = :summary WHERE p.id = :personalInfoId")
    int updatePersonalInfoBasic(@Param("personalInfoId") UUID personalInfoId,
                                @Param("fullname") String fullname,
                                @Param("email") String email,
                                @Param("phone") String phone,
                                @Param("location") String location,
                                @Param("birth") String birth,
                                @Param("summary") String summary);

    /**
     * Cập nhật avatar của personal info
     */
    @Modifying
    @Transactional
    @Query("UPDATE PersonalInfo p SET p.avatarUrl = :avatarUrl, p.avatarPublicId = :avatarPublicId WHERE p.id = :personalInfoId")
    int updatePersonalInfoAvatar(@Param("personalInfoId") UUID personalInfoId,
                                  @Param("avatarUrl") String avatarUrl,
                                  @Param("avatarPublicId") String avatarPublicId);
}
