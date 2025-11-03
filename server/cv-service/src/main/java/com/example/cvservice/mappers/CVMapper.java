package com.example.cvservice.mappers;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.Education;
import com.example.cvservice.entities.Experience;
import com.example.cvservice.entities.CV.CVPrivacy;

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
        if (cv.getExperiences() != null) {
            experiences = cv.getExperiences().stream().map(experienceMapper::toDto).collect(Collectors.toList());
        }
        dto.setExperiences(experiences);

        List<EducationDto> educations = null;
        if (cv.getEducations() != null) {
            educations = cv.getEducations().stream().map(educationMapper::toDto).collect(Collectors.toList());
        }
        dto.setEducations(educations);

        dto.setSkills(cv.getSkills());

        dto.setPrivacy(cv.getPrivacy() != null ? cv.getPrivacy().name() : null);

        dto.setColor(cv.getColor());
        dto.setTemplate(cv.getTemplate());

        dto.setCreatedAt(cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : null);
        dto.setUpdatedAt(cv.getUpdatedAt() != null ? cv.getUpdatedAt().toString() : null);

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

        cv.setColor(dto.getColor());
        cv.setTemplate(dto.getTemplate());

        if (dto.getPrivacy() != null) {
            try {
                cv.setPrivacy(CVPrivacy.valueOf(dto.getPrivacy()));
            } catch (IllegalArgumentException e) {
                cv.setPrivacy(CVPrivacy.PRIVATE);
            }
        }
        if (dto.getUpdatedAt() != null) {
            try {
                cv.setUpdatedAt(Instant.parse(dto.getUpdatedAt()));
            } catch (Exception ignored) {
            }
        }

        return cv;
    }
}