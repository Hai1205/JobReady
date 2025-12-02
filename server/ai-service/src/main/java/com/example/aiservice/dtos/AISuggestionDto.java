package com.example.aiservice.dtos;

import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;

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
    private String before; // Original content before applying suggestion (for Job Match API)
    private JsonNode data; // Actual data to apply directly (skills array, summary text, dates, etc.)
    private Boolean applied;
}
