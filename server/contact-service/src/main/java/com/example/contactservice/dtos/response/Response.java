package com.example.contactservice.dtos.response;

import java.util.List;
import java.util.Map;

import com.example.contactservice.dtos.ContactDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;
    private String message;

    // Contact related data
    private ContactDto contact;
    private List<ContactDto> contacts;

    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Authentication related
    private Boolean authenticated;
    private String expirationTime;

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