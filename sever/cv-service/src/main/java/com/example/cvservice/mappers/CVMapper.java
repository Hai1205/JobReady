package com.example.cvservice.mappers;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.Education;
import com.example.cvservice.entities.Experience;

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
        CVDto dto = new CVDto();
        dto.setId(cv.getId());
        dto.setUserId(cv.getUserId());
        dto.setTitle(cv.getTitle());
        dto.setPersonalInfo(personalInfoMapper.toDto(cv.getPersonalInfo()));

        List<ExperienceDto> experiences = null;
        if (cv.getExperience() != null) {
            experiences = cv.getExperience().stream().map(experienceMapper::toDto).collect(Collectors.toList());
        }
        dto.setExperience(experiences);

        List<EducationDto> educations = null;
        if (cv.getEducation() != null) {
            educations = cv.getEducation().stream().map(educationMapper::toDto).collect(Collectors.toList());
        }
        dto.setEducation(educations);

        dto.setSkills(cv.getSkills());
        dto.setCreatedAt(cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : null);
        dto.setUpdatedAt(cv.getUpdatedAt() != null ? cv.getUpdatedAt().toString() : null);

        return dto;
    }

    public CV toEntity(CVDto cvDto) {
        if (cvDto == null)
            return null;
        CV cv = new CV();
        cv.setId(cvDto.getId());
        cv.setUserId(cvDto.getUserId());
        cv.setTitle(cvDto.getTitle());
        cv.setPersonalInfo(personalInfoMapper.toEntity(cvDto.getPersonalInfo()));

        if (cvDto.getExperience() != null) {
            List<Experience> ex = cvDto.getExperience().stream().map(exDto -> experienceMapper.toEntity(exDto))
                    .collect(Collectors.toList());
            cv.setExperience(ex);
        }

        if (cvDto.getEducation() != null) {
            List<Education> ed = cvDto.getEducation().stream().map(edDto -> educationMapper.toEntity(edDto))
                    .collect(Collectors.toList());
            cv.setEducation(ed);
        }

        cv.setSkills(cvDto.getSkills());
        if (cvDto.getCreatedAt() != null) {
            try {
                cv.setCreatedAt(Instant.parse(cvDto.getCreatedAt()));
            } catch (Exception ignored) {
            }
        }
        if (cvDto.getUpdatedAt() != null) {
            try {
                cv.setUpdatedAt(Instant.parse(cvDto.getUpdatedAt()));
            } catch (Exception ignored) {
            }
        }

        return cv;
    }
}