package com.example.aiservice.dtos.responses;

import java.util.List;
import java.util.Map;

import com.example.aiservice.dtos.CVDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CVResponse {
    private int statusCode;
    private String message;

    private CVDto cv;
    private List<CVDto> cvs;

    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Generic data container for any other service-specific data
    private Map<String, Object> additionalData;

    public CVResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public CVResponse() {
        this.statusCode = 200;
    }
}