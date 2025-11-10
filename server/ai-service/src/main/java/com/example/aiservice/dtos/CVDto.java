package com.example.aiservice.dtos;

import java.util.List;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CVDto {
    private UUID id;
    private UUID userId;
    private String title;
    private PersonalInfoDto personalInfo;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<String> skills;
    private Boolean isVisibility;
    private String color;
    private String template;
    private String createdAt;
    private String updatedAt;
}
