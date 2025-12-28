package com.example.cvservice.mappers;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.PersonalInfoDto;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.Education;
import com.example.cvservice.entities.Experience;
import com.example.cvservice.entities.PersonalInfo;
import com.example.cvservice.repositories.personalInfoRepositories.PersonalInfoQueryRepository;
import com.example.cvservice.repositories.experienceRepositories.ExperienceQueryRepository;
import com.example.cvservice.repositories.educationRepositories.EducationQueryRepository;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Component
public class CVMapper {

    private final PersonalInfoMapper personalInfoMapper;
    private final ExperienceMapper experienceMapper;
    private final EducationMapper educationMapper;
    private final PersonalInfoQueryRepository personalInfoQueryRepository;
    private final ExperienceQueryRepository experienceQueryRepository;
    private final EducationQueryRepository educationQueryRepository;

    public CVMapper(PersonalInfoMapper personalInfoMapper, 
                   ExperienceMapper experienceMapper,
                   EducationMapper educationMapper,
                   PersonalInfoQueryRepository personalInfoQueryRepository,
                   ExperienceQueryRepository experienceQueryRepository,
                   EducationQueryRepository educationQueryRepository) {
        this.personalInfoMapper = personalInfoMapper;
        this.experienceMapper = experienceMapper;
        this.educationMapper = educationMapper;
        this.personalInfoQueryRepository = personalInfoQueryRepository;
        this.experienceQueryRepository = experienceQueryRepository;
        this.educationQueryRepository = educationQueryRepository;
    }

    /**
     * Convert CV entity to DTO
     * Note: PersonalInfo, Experience, Education are separate entities now
     */
    public CVDto toDto(CV cv) {
        if (cv == null)
            return null;
        
        CVDto dto = new CVDto();
        dto.setId(cv.getId());
        dto.setUserId(cv.getUserId());
        dto.setTitle(cv.getTitle());

        // Fetch PersonalInfo from repository
        Optional<PersonalInfo> personalInfo = personalInfoQueryRepository.findByCvId(cv.getId());
        dto.setPersonalInfo(personalInfo.map(personalInfoMapper::toDto).orElse(null));

        // Fetch Experiences from repository
        List<Experience> experiences = experienceQueryRepository.findByCvId(cv.getId());
        List<ExperienceDto> experienceDtos = experiences != null && !experiences.isEmpty()
                ? experiences.stream().map(experienceMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();
        dto.setExperiences(experienceDtos);

        // Fetch Educations from repository
        List<Education> educations = educationQueryRepository.findByCvId(cv.getId());
        List<EducationDto> educationDtos = educations != null && !educations.isEmpty()
                ? educations.stream().map(educationMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();
        dto.setEducations(educationDtos);

        // Skills are still part of CV entity
        List<String> skills = cv.getSkills();
        dto.setSkills(skills != null ? skills : Collections.emptyList());

        dto.setIsVisibility(cv.getIsVisibility() != null ? cv.getIsVisibility() : false);
        dto.setColor(cv.getColor() != null ? cv.getColor() : "#3498db");
        dto.setTemplate(cv.getTemplate() != null ? cv.getTemplate() : "modern");
        dto.setFont(cv.getFont() != null ? cv.getFont() : "Inter, sans-serif");

        dto.setCreatedAt(cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : null);
        dto.setUpdatedAt(cv.getUpdatedAt() != null ? cv.getUpdatedAt().toString() : null);
        
        return dto;
    }

    /**
     * Convert CV DTO to entity (basic fields only)
     * Note: Child entities need to be saved separately
     */
    public CV toEntity(CVDto dto) {
        if (dto == null)
            return null;
            
        CV cv = new CV();
        cv.setId(dto.getId());
        cv.setUserId(dto.getUserId());
        cv.setTitle(dto.getTitle());
        cv.setSkills(dto.getSkills());
        
        if (dto.getCreatedAt() != null) {
            try {
                cv.setCreatedAt(Instant.parse(dto.getCreatedAt()));
            } catch (Exception ignored) {
            }
        }

        cv.setColor(dto.getColor() != null ? dto.getColor() : "#3498db");
        cv.setTemplate(dto.getTemplate() != null ? dto.getTemplate() : "modern");
        cv.setFont(dto.getFont() != null ? dto.getFont() : "Inter, sans-serif");
        cv.setIsVisibility(dto.getIsVisibility() != null ? dto.getIsVisibility() : false);

        if (dto.getUpdatedAt() != null) {
            try {
                cv.setUpdatedAt(Instant.parse(dto.getUpdatedAt()));
            } catch (Exception ignored) {
            }
        }

        return cv;
    }
}