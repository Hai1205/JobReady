package com.example.cvservice.mappers;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.Education;
import com.example.cvservice.entities.Experience;
import com.example.cvservice.entities.PersonalInfo;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CVMapper {

    private final PersonalInfoMapper personalInfoMapper;
    private final ExperienceMapper experienceMapper;
    private final EducationMapper educationMapper;

    public CVMapper(PersonalInfoMapper personalInfoMapper, ExperienceMapper experienceMapper,
            EducationMapper educationMapper) {
        this.personalInfoMapper = personalInfoMapper;
        this.experienceMapper = experienceMapper;
        this.educationMapper = educationMapper;
    }

    public CVDto toDto(CV cv) {
        if (cv == null)
            return null;
        
        System.out.println("=== CVMapper.toDto START ===");
        System.out.println("CV ID: " + cv.getId());
        System.out.println("CV Title: " + cv.getTitle());
        System.out.println("CV Color: " + cv.getColor());
        System.out.println("CV Template: " + cv.getTemplate());
        System.out.println("CV IsVisibility: " + cv.getIsVisibility());
        // Removed skills.size() call here - it causes LazyInitializationException
        System.out.println("CV PersonalInfo: " + (cv.getPersonalInfo() != null ? "NOT NULL" : "NULL"));
        
        CVDto dto = new CVDto();
        dto.setId(cv.getId());
        dto.setUserId(cv.getUserId());
        dto.setTitle(cv.getTitle());

        // Always set personalInfo, even if null (client will handle it)
        PersonalInfo pi = cv.getPersonalInfo();
        System.out.println("PersonalInfo before mapping: " + (pi != null ? "NOT NULL - " + pi.getEmail() : "NULL"));
        dto.setPersonalInfo(personalInfoMapper.toDto(pi));
        System.out.println("PersonalInfo after mapping: " + (dto.getPersonalInfo() != null ? "NOT NULL" : "NULL"));

        // Return empty list instead of null for experiences
        List<ExperienceDto> experiences = java.util.Collections.emptyList();
        List<Experience> expEntities = cv.getExperiences();
        System.out.println("Experiences entities: " + (expEntities != null ? expEntities.size() : "NULL"));
        if (expEntities != null && !expEntities.isEmpty()) {
            experiences = expEntities.stream()
                    .map(experienceMapper::toDto)
                    .collect(Collectors.toList());
        }
        dto.setExperiences(experiences);
        System.out.println("Experiences DTOs: " + experiences.size());

        // Return empty list instead of null for educations
        List<EducationDto> educations = java.util.Collections.emptyList();
        List<Education> eduEntities = cv.getEducations();
        System.out.println("Educations entities: " + (eduEntities != null ? eduEntities.size() : "NULL"));
        if (eduEntities != null && !eduEntities.isEmpty()) {
            educations = eduEntities.stream()
                    .map(educationMapper::toDto)
                    .collect(Collectors.toList());
        }
        dto.setEducations(educations);
        System.out.println("Educations DTOs: " + educations.size());

        // Return empty list instead of null for skills
        List<String> skills = cv.getSkills();
        System.out.println("Skills from entity: " + (skills != null ? skills.size() + " items" : "NULL"));
        dto.setSkills(skills != null ? skills : java.util.Collections.emptyList());

        dto.setIsVisibility(cv.getIsVisibility() != null ? cv.getIsVisibility() : false);

        dto.setColor(cv.getColor() != null ? cv.getColor() : "#3498db");
        dto.setTemplate(cv.getTemplate() != null ? cv.getTemplate() : "modern");

        dto.setCreatedAt(cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : null);
        dto.setUpdatedAt(cv.getUpdatedAt() != null ? cv.getUpdatedAt().toString() : null);

        System.out.println("=== CVMapper.toDto END ===");
        System.out.println("DTO Color: " + dto.getColor());
        System.out.println("DTO Template: " + dto.getTemplate());
        System.out.println("DTO IsVisibility: " + dto.getIsVisibility());
        System.out.println("DTO Skills: " + (dto.getSkills() != null ? dto.getSkills().size() : "NULL"));
        System.out.println("DTO PersonalInfo: " + (dto.getPersonalInfo() != null ? "NOT NULL" : "NULL"));
        
        return dto;
    }

    public CV toEntity(CVDto dto) {
        if (dto == null)
            return null;
        CV cv = new CV();
        cv.setId(dto.getId());
        cv.setUserId(dto.getUserId());
        cv.setTitle(dto.getTitle());
        cv.setPersonalInfo(personalInfoMapper.toEntity(dto.getPersonalInfo()));

        if (dto.getExperiences() != null) {
            List<Experience> ex = dto.getExperiences().stream().map(exDto -> experienceMapper.toEntity(exDto))
                    .collect(Collectors.toList());
            cv.setExperiences(ex);
        }

        if (dto.getEducations() != null) {
            List<Education> ed = dto.getEducations().stream().map(edDto -> educationMapper.toEntity(edDto))
                    .collect(Collectors.toList());
            cv.setEducations(ed);
        }

        cv.setSkills(dto.getSkills());
        if (dto.getCreatedAt() != null) {
            try {
                cv.setCreatedAt(Instant.parse(dto.getCreatedAt()));
            } catch (Exception ignored) {
            }
        }

        cv.setColor(dto.getColor() != null ? dto.getColor() : "#3498db");
        cv.setTemplate(dto.getTemplate() != null ? dto.getTemplate() : "modern");
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