package com.example.aiservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDescriptionResult {
    private String jobTitle;
    private String company;
    private String jobLevel;
    private String jobType;
    private String salary;
    private String location;
    private List<String> responsibilities;
    private List<String> requirements;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private List<String> benefits;
}
