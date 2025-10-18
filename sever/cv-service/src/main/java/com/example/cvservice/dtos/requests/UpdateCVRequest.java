package com.example.cvservice.dtos.requests;

import java.util.List;

import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.PersonalInfoDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCVRequest {
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experience;
    private List<EducationDto> education;
    private List<String> skills;
}
