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

    /**
     * Access Token (alias cho token field)
     * Để rõ ràng hơn khi trả về response
     */
    private String accessToken;

    /**
     * Refresh Token (JWT dài hạn - 7 ngày)
     * Được sử dụng để lấy access token mới khi access token hết hạn
     */
    private String refreshToken;

    /**
     * Thông tin user
     */
    private UserDto user;

    /**
     * Additional data (flexible field)
     */
    private Map<String, Object> additionalData;
}