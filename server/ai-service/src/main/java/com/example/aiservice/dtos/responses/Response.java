package com.example.aiservice.dtos.responses;

import java.util.List;
import java.util.Map;

import com.example.aiservice.dtos.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;
    private String message;

    private AnalyzeResultDto analyze;
    private String improvedSection;
    private List<AISuggestionDto> suggestions;
    private String extractedText;
    private Double matchScore;
    private Object parsedJobDescription;
    private List<String> missingKeywords;

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