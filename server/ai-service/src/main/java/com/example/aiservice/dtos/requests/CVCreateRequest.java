package com.example.aiservice.dtos.requests;

import java.util.List;

import com.example.aiservice.dtos.EducationDto;
import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.dtos.PersonalInfoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVCreateRequest {
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<String> skills;
}