package com.example.cvservice.mapper;

import com.example.cvservice.dto.ExperienceDto;
import com.example.cvservice.entity.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {
    ExperienceDto toDto(Experience experience);

    Experience toEntity(ExperienceDto experienceDto);
}