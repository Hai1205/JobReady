package com.example.cvservice.dtos.requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.PersonalInfoDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVCreateRequest {
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<String> skills;
    private Boolean isVisibility;
    private String color;
    private String template;
}