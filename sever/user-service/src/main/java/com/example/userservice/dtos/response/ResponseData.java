package com.example.userservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    // User related data
    private Object user;
    private List<?> users;
    private String token;
    private String role;
    private String status;

    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Authentication related
    private Boolean authenticated;
    private String expirationTime;

    // Generic data container for any other service-specific data
    private Map<String, Object> additionalData;
}