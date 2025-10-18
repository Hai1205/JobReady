package com.example.cvservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import lombok.*;

/**
 * Generic data container for all service responses
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    // CV related data
    private Object cv;
    private List<?> cvs;
    private Object experience;
    private List<?> experiences;
    private Object education;
    private List<?> educations;
    private List<?> skills;

    // AI Analysis and Improvement
    private String analyze;
    private String improvedSection;
    private List<?> suggestions;
    private String extractedText;
    private Double matchScore;
    private Object parsedJobDescription;
    private List<String> missingKeywords;

    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Generic data container for any other service-specific data
    private Map<String, Object> additionalData;
}