package com.example.cvservice.mapper;

import com.example.cvservice.dto.EducationDto;
import com.example.cvservice.entity.Education;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    EducationDto toDto(Education education);

    Education toEntity(EducationDto educationDto);
}