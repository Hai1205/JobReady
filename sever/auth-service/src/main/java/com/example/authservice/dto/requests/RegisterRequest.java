package com.example.authservice.dto.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String fullname;
    private String email;
    private String password;
}