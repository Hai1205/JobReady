package com.example.cvservice.dtos.responses;

import java.util.List;
import java.util.Map;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;
    private String message;

    private UserDto user;
    private List<UserDto> users;
    private String token;
    private String role;
    private String status;
    private CVDto cv;
    private List<CVDto> cvs;
    private CVDto experience;
    private List<CVDto> experiences;
    private CVDto education;
    private List<CVDto> educations;
    private List<String> skills;

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