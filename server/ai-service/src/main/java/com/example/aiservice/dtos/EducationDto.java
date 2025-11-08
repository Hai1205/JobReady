package com.example.aiservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDto {
    private UUID id;
    private String school;
    private String degree;
    private String field;
    private String startDate;
    private String endDate;
}
