package com.example.cvservice.mappers;

import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.entities.Experience;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExperienceMapper {

    public ExperienceDto toDto(Experience experience) {
        if (experience == null)
            return null;
        ExperienceDto dto = new ExperienceDto();
        dto.setId(experience.getId());
        dto.setCompany(experience.getCompany());
        dto.setPosition(experience.getPosition());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setDescription(experience.getDescription());
        return dto;
    }

    public Experience toEntity(ExperienceDto experienceDto) {
        if (experienceDto == null)
            return null;
        Experience e = new Experience();
        e.setId(experienceDto.getId() != null ? experienceDto.getId() : UUID.randomUUID());
        e.setCompany(experienceDto.getCompany());
        e.setPosition(experienceDto.getPosition());
        e.setStartDate(experienceDto.getStartDate());
        e.setEndDate(experienceDto.getEndDate());
        e.setDescription(experienceDto.getDescription());
        return e;
    }
}