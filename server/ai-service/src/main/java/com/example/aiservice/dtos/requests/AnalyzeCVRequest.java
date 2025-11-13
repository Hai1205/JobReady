package com.example.aiservice.dtos.requests;

import java.util.List;

import com.example.aiservice.dtos.EducationDto;
import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.dtos.PersonalInfoDto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzeCVRequest {
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<String> skills;
    private String privacy;
    private String color;
    private String template;
}
