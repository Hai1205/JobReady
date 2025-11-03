package com.example.cvservice.dtos.requests;

import java.util.List;

import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.PersonalInfoDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCVRequest {
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<String> skills;
    private String privacy;
    private String color;
    private String template;
}
