package com.example.userservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.User;
import com.example.userservice.entities.User.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserQueryRepository - Sử dụng @Query với JPQL để truy vấn
 * Tất cả các operations READ sẽ được thực hiện qua repository này
 */
@Repository
public interface UserQueryRepository extends JpaRepository<User, UUID> {

    /**
     * Tìm user theo username
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Tìm user theo email
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Kiểm tra username đã tồn tại
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Kiểm tra email đã tồn tại
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Tìm user theo email và OAuth provider
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.oauthProvider = :oauthProvider")
    Optional<User> findByEmailAndOauthProvider(@Param("email") String email,
            @Param("oauthProvider") String oauthProvider);

    /**
     * Tìm user theo OAuth provider và provider ID
     */
    @Query("SELECT u FROM User u WHERE u.oauthProvider = :oauthProvider AND u.oauthProviderId = :oauthProviderId")
    Optional<User> findByOauthProviderAndOauthProviderId(@Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderId") String oauthProviderId);

    /**
     * Kiểm tra email và OAuth provider đã tồn tại
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.oauthProvider = :oauthProvider")
    boolean existsByEmailAndOauthProvider(@Param("email") String email,
            @Param("oauthProvider") String oauthProvider);

    /**
     * Lấy tổng số users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    /**
     * Đếm users theo status
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    /**
     * Đếm users được tạo trong khoảng thời gian
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Lấy danh sách users mới nhất với phân trang
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(Pageable pageable);

    /**
     * Lấy tất cả users với phân trang
     */
    @Query("SELECT u FROM User u")
    Page<User> findAllUsers(Pageable pageable);

    /**
     * Lấy users theo status với phân trang
     */
    @Query("SELECT u FROM User u WHERE u.status = :status")
    Page<User> findUsersByStatus(@Param("status") UserStatus status, Pageable pageable);
}
