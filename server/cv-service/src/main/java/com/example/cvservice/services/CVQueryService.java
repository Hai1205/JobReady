package com.example.cvservice.services;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.PersonalInfoDto;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.Education;
import com.example.cvservice.entities.Experience;
import com.example.cvservice.entities.PersonalInfo;
import com.example.cvservice.repositories.cvRepositories.CVQueryRepository;
import com.example.cvservice.repositories.educationRepositories.EducationQueryRepository;
import com.example.cvservice.repositories.experienceRepositories.ExperienceQueryRepository;
import com.example.cvservice.repositories.personalInfoRepositories.PersonalInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CVQueryService - Service layer để fetch CV với đầy đủ children entities
 * Tự động JOIN fetch PersonalInfo, Experience, Education bằng cvId
 * Trả về DTO thay vì Entity
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CVQueryService {

    private final CVQueryRepository cvQueryRepository;
    private final PersonalInfoQueryRepository personalInfoQueryRepository;
    private final ExperienceQueryRepository experienceQueryRepository;
    private final EducationQueryRepository educationQueryRepository;

    /**
     * Tìm CV theo ID và fetch tất cả children
     */
    public Optional<CVDto> findByIdWithChildren(UUID id) {
        return cvQueryRepository.findById(id)
                .map(this::enrichCVWithChildren);
    }

    /**
     * Tìm tất cả CV của user với children và phân trang
     */
    public Page<CVDto> findAllByUserIdWithChildren(UUID userId, Pageable pageable) {
        Page<CV> cvPage = cvQueryRepository.findAllByUserId(userId, pageable);
        List<CVDto> cvDtos = cvPage.getContent().stream()
                .map(this::enrichCVWithChildren)
                .collect(Collectors.toList());
        return new PageImpl<>(cvDtos, pageable, cvPage.getTotalElements());
    }

    /**
     * Lấy tất cả CVs với children và phân trang
     */
    public Page<CVDto> findAllWithChildren(Pageable pageable) {
        Page<CV> cvPage = cvQueryRepository.findAllCVs(pageable);
        List<CVDto> cvDtos = cvPage.getContent().stream()
                .map(this::enrichCVWithChildren)
                .collect(Collectors.toList());
        return new PageImpl<>(cvDtos, pageable, cvPage.getTotalElements());
    }

    /**
     * Lấy CVs theo visibility với children và phân trang
     */
    public Page<CVDto> findByVisibilityWithChildren(boolean isVisibility, Pageable pageable) {
        Page<CV> cvPage = cvQueryRepository.findByVisibility(isVisibility, pageable);
        List<CVDto> cvDtos = cvPage.getContent().stream()
                .map(this::enrichCVWithChildren)
                .collect(Collectors.toList());
        return new PageImpl<>(cvDtos, pageable, cvPage.getTotalElements());
    }

    /**
     * Lấy CVs mới nhất với children và phân trang
     */
    public Page<CVDto> findRecentCVsWithChildren(Pageable pageable) {
        Page<CV> cvPage = cvQueryRepository.findRecentCVs(pageable);
        List<CVDto> cvDtos = cvPage.getContent().stream()
                .map(this::enrichCVWithChildren)
                .collect(Collectors.toList());
        return new PageImpl<>(cvDtos, pageable, cvPage.getTotalElements());
    }

    /**
     * Helper method: Fetch children và build CVDto
     * Thực hiện JOIN logic bằng cách query children theo cvId
     */
    private CVDto enrichCVWithChildren(CV cv) {
        // Fetch PersonalInfo
        PersonalInfo personalInfo = personalInfoQueryRepository.findByCvId(cv.getId())
                .orElse(null);
        
        // Fetch Experiences
        List<Experience> experiences = experienceQueryRepository.findByCvId(cv.getId());
        
        // Fetch Educations
        List<Education> educations = educationQueryRepository.findByCvId(cv.getId());

        // Convert entities to DTOs
        PersonalInfoDto personalInfoDto = personalInfo != null ? convertToDto(personalInfo) : null;
        List<ExperienceDto> experienceDtos = experiences.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        List<EducationDto> educationDtos = educations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Build CVDto - CVDto has @Builder
        return CVDto.builder()
                .id(cv.getId())
                .userId(cv.getUserId())
                .title(cv.getTitle())
                .personalInfo(personalInfoDto)
                .experiences(experienceDtos)
                .educations(educationDtos)
                .skills(cv.getSkills())
                .isVisibility(cv.getIsVisibility())
                .color(cv.getColor())
                .template(cv.getTemplate())
                .font(cv.getFont())
                .createdAt(cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : null)
                .updatedAt(cv.getUpdatedAt() != null ? cv.getUpdatedAt().toString() : null)
                .build();
    }

    /**
     * Convert PersonalInfo entity to DTO
     */
    private PersonalInfoDto convertToDto(PersonalInfo entity) {
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setId(entity.getId());
        dto.setFullname(entity.getFullname());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setLocation(entity.getLocation());
        dto.setBirth(entity.getBirth());
        dto.setSummary(entity.getSummary());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setAvatarPublicId(entity.getAvatarPublicId());
        return dto;
    }

    /**
     * Convert Experience entity to DTO
     */
    private ExperienceDto convertToDto(Experience entity) {
        ExperienceDto dto = new ExperienceDto();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setPosition(entity.getPosition());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    /**
     * Convert Education entity to DTO
     */
    private EducationDto convertToDto(Education entity) {
        EducationDto dto = new EducationDto();
        dto.setId(entity.getId());
        dto.setSchool(entity.getSchool());
        dto.setDegree(entity.getDegree());
        dto.setField(entity.getField());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    // Các method không cần children có thể delegate trực tiếp
    public long countTotalCVs() {
        return cvQueryRepository.countTotalCVs();
    }

    public long countByVisibility(boolean isVisibility) {
        return cvQueryRepository.countByVisibility(isVisibility);
    }

    public boolean existsByUserId(UUID userId) {
        return cvQueryRepository.existsByUserId(userId);
    }

    public long countByUserId(UUID userId) {
        return cvQueryRepository.countByUserId(userId);
    }
}
