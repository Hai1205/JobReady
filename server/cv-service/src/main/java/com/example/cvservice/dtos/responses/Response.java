package com.example.cvservice.dtos.responses;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;
    private String message;

     // CV related data
    private Object cv;
    private List<?> cvs;
    private Object experience;
    private List<?> experiences;
    private Object education;
    private List<?> educations;
    private List<?> skills;

    // AI Analyze and Improvement
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

    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response() {
        this.statusCode = 200;
    }
}