package com.example.cvservice.repositories.cvRepositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.entities.CV;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CVQueryRepository - Sử dụng @Query với JPQL để truy vấn
 * Tất cả các operations READ sẽ được thực hiện qua repository này
 * Trả về DTO thay vì Entity để tách biệt persistence và business logic
 */
@Repository
public interface CVQueryRepository extends JpaRepository<CV, UUID> {

    /**
     * Tìm CV theo title (trả về entity cho simple operations)
     */
    @Query("SELECT c FROM CV c WHERE c.title = :title")
    Optional<CV> findByTitle(@Param("title") String title);

    /**
     * Tìm tất cả CV của user với phân trang (trả về entity, mapper sẽ fetch children)
     */
    @Query("SELECT c FROM CV c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    Page<CV> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Lấy tất cả CVs với phân trang (trả về entity, mapper sẽ fetch children)
     */
    @Query("SELECT c FROM CV c ORDER BY c.createdAt DESC")
    Page<CV> findAllCVs(Pageable pageable);

    /**
     * Lấy tổng số CVs
     */
    @Query("SELECT COUNT(c) FROM CV c")
    long countTotalCVs();

    /**
     * Lấy CVs theo visibility với phân trang (trả về entity, mapper sẽ fetch children)
     */
    @Query("SELECT c FROM CV c WHERE c.isVisibility = :isVisibility ORDER BY c.createdAt DESC")
    Page<CV> findByVisibility(@Param("isVisibility") boolean isVisibility, Pageable pageable);

    /**
     * Đếm CVs theo visibility
     */
    @Query("SELECT COUNT(c) FROM CV c WHERE c.isVisibility = :isVisibility")
    long countByVisibility(@Param("isVisibility") boolean isVisibility);

    /**
     * Lấy CVs được tạo trong khoảng thời gian với phân trang
     */
    @Query("SELECT c FROM CV c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    Page<CV> findCVsCreatedBetween(@Param("startDate") Instant startDate, 
                                    @Param("endDate") Instant endDate,
                                    Pageable pageable);

    /**
     * Đếm CVs được tạo trong khoảng thời gian
     */
    @Query("SELECT COUNT(c) FROM CV c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    long countCVsCreatedBetween(@Param("startDate") Instant startDate, 
                                 @Param("endDate") Instant endDate);

    /**
     * Lấy CVs mới nhất với phân trang (trả về entity, mapper sẽ fetch children)
     */
    @Query("SELECT c FROM CV c ORDER BY c.createdAt DESC")
    Page<CV> findRecentCVs(Pageable pageable);

    /**
     * Kiểm tra user có CV nào không
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CV c WHERE c.userId = :userId")
    boolean existsByUserId(@Param("userId") UUID userId);

    /**
     * Đếm số CV của user
     */
    @Query("SELECT COUNT(c) FROM CV c WHERE c.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);
}
