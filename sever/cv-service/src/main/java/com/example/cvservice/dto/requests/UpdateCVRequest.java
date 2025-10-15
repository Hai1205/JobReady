package com.example.cvservice.dto.requests;

import java.util.List;

import com.example.cvservice.dto.EducationDto;
import com.example.cvservice.dto.ExperienceDto;
import com.example.cvservice.dto.PersonalInfoDto;

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
