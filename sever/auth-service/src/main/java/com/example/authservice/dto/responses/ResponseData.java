package com.example.authservice.dto.responses;

import java.util.Map;

import com.example.authservice.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    private String token;

    private UserDto user;

    private Map<String, Object> additionalData;
}