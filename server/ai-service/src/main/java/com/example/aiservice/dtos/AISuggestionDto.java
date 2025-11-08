package com.example.aiservice.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AISuggestionDto {
    private String id;
    private String type; // "improvement", "warning", "error"
    private String section; // "summary", "experience", "education", "skills"
    private Integer lineNumber;
    private String message;
    private String suggestion;
    private Boolean applied;
}
