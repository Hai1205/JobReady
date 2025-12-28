package com.example.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.User;
import com.example.userservice.entities.User.UserRole;
import com.example.userservice.entities.User.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserCommandRepository - Sử dụng @Modifying cho các operations CREATE, UPDATE,
 * DELETE
 * Tất cả các operations ghi (write) sẽ được thực hiện qua repository này
 */
@Repository
public interface UserCommandRepository extends JpaRepository<User, UUID> {

    /**
     * Cập nhật status của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") UUID userId,
            @Param("status") UserStatus status,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật status của user theo email
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = :updatedAt WHERE u.email = :email")
    int updateUserStatusByEmail(@Param("email") String email,
            @Param("status") UserStatus status,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật password của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserPassword(@Param("userId") UUID userId,
            @Param("password") String password,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật password theo email
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = :updatedAt WHERE u.email = :email")
    int updateUserPasswordByEmail(@Param("email") String email,
            @Param("password") String password,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật thông tin cơ bản của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullname = :fullname, u.phone = :phone, u.location = :location, " +
            "u.birth = :birth, u.summary = :summary, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserBasicInfo(@Param("userId") UUID userId,
            @Param("fullname") String fullname,
            @Param("phone") String phone,
            @Param("location") String location,
            @Param("birth") String birth,
            @Param("summary") String summary,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật role của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = :role, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserRole(@Param("userId") UUID userId,
            @Param("role") UserRole role,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật avatar của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.avatarUrl = :avatarUrl, u.avatarPublicId = :avatarPublicId, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserAvatar(@Param("userId") UUID userId,
            @Param("avatarUrl") String avatarUrl,
            @Param("avatarPublicId") String avatarPublicId,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật thông tin OAuth2 của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.oauthProvider = :oauthProvider, u.oauthProviderId = :oauthProviderId, " +
            "u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserOAuth2Info(@Param("userId") UUID userId,
            @Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderId") String oauthProviderId,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Xóa user theo ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :userId")
    int deleteUserById(@Param("userId") UUID userId);

    /**
     * Xóa user theo email
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.email = :email")
    int deleteUserByEmail(@Param("email") String email);

    /**
     * Cập nhật fullname của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullname = :fullname, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserFullname(@Param("userId") UUID userId,
            @Param("fullname") String fullname,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật phone của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.phone = :phone, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserPhone(@Param("userId") UUID userId,
            @Param("phone") String phone,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật location của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.location = :location, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserLocation(@Param("userId") UUID userId,
            @Param("location") String location,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật birth của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.birth = :birth, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserBirth(@Param("userId") UUID userId,
            @Param("birth") String birth,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Cập nhật summary của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.summary = :summary, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserSummary(@Param("userId") UUID userId,
            @Param("summary") String summary,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Insert user mới (basic user with password)
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (id, username, email, password, fullname, phone, location, birth, summary, " +
            "avatar_url, avatar_public_id, role, status, created_at, updated_at) " +
            "VALUES (:id, :username, :email, :password, :fullname, :phone, :location, :birth, :summary, " +
            ":avatarUrl, :avatarPublicId, :role, :status, :createdAt, :updatedAt)", nativeQuery = true)
    int insertUser(@Param("id") UUID id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("password") String password,
            @Param("fullname") String fullname,
            @Param("phone") String phone,
            @Param("location") String location,
            @Param("birth") String birth,
            @Param("summary") String summary,
            @Param("avatarUrl") String avatarUrl,
            @Param("avatarPublicId") String avatarPublicId,
            @Param("role") String role,
            @Param("status") String status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Insert OAuth2 user
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (id, username, email, fullname, oauth_provider, oauth_provider_id, " +
            "avatar_url, role, status, is_oauth_user, created_at, updated_at) " +
            "VALUES (:id, :username, :email, :fullname, :oauthProvider, :oauthProviderId, " +
            ":avatarUrl, :role, :status, :isOAuthUser, :createdAt, :updatedAt)", nativeQuery = true)
    int insertOAuth2User(@Param("id") UUID id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("fullname") String fullname,
            @Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderId") String oauthProviderId,
            @Param("avatarUrl") String avatarUrl,
            @Param("role") String role,
            @Param("status") String status,
            @Param("isOAuthUser") boolean isOAuthUser,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Update user with all fields (for complex updates)
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullname = :fullname, u.phone = :phone, u.location = :location, " +
            "u.birth = :birth, u.summary = :summary, u.avatarUrl = :avatarUrl, u.avatarPublicId = :avatarPublicId, " +
            "u.role = :role, u.status = :status, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserAllFields(@Param("userId") UUID userId,
            @Param("fullname") String fullname,
            @Param("phone") String phone,
            @Param("location") String location,
            @Param("birth") String birth,
            @Param("summary") String summary,
            @Param("avatarUrl") String avatarUrl,
            @Param("avatarPublicId") String avatarPublicId,
            @Param("role") UserRole role,
            @Param("status") UserStatus status,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Update OAuth2 user info
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullname = :fullname, u.avatarUrl = :avatarUrl, " +
            "u.oauthProvider = :oauthProvider, u.oauthProviderId = :oauthProviderId, " +
            "u.isOAuthUser = :isOAuthUser, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateOAuth2User(@Param("userId") UUID userId,
            @Param("fullname") String fullname,
            @Param("avatarUrl") String avatarUrl,
            @Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderId") String oauthProviderId,
            @Param("isOAuthUser") boolean isOAuthUser,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Link OAuth2 provider to existing user
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.oauthProvider = :oauthProvider, u.oauthProviderId = :oauthProviderId, " +
            "u.avatarUrl = :avatarUrl, u.isOAuthUser = :isOAuthUser, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int linkOAuth2Provider(@Param("userId") UUID userId,
            @Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderId") String oauthProviderId,
            @Param("avatarUrl") String avatarUrl,
            @Param("isOAuthUser") boolean isOAuthUser,
            @Param("updatedAt") LocalDateTime updatedAt);
}
