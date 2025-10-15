package com.example.authservice.dto.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthErrorRequest {
    private String error;
    private String errorDescription;
}
