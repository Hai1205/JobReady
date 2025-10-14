package com.example.cvservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDto {
    private UUID id;
    private String company;
    private String position;
    private String startDate;
    private String endDate;
    private String description;
}
