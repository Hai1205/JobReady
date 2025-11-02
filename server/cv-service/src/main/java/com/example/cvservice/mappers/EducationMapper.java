package com.example.cvservice.mappers;

import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.entities.Education;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EducationMapper {

    public EducationDto toDto(Education education) {
        if (education == null)
            return null;
        EducationDto dto = new EducationDto();
        dto.setId(education.getId());
        dto.setSchool(education.getSchool());
        dto.setDegree(education.getDegree());
        dto.setField(education.getField());
        dto.setStartDate(education.getStartDate());
        dto.setEndDate(education.getEndDate());
        return dto;
    }

    public Education toEntity(EducationDto educationDto) {
        if (educationDto == null)
            return null;
        Education e = new Education();
        e.setId(educationDto.getId() != null ? educationDto.getId() : UUID.randomUUID());
        e.setSchool(educationDto.getSchool());
        e.setDegree(educationDto.getDegree());
        e.setField(educationDto.getField());
        e.setStartDate(educationDto.getStartDate());
        e.setEndDate(educationDto.getEndDate());
        return e;
    }
}