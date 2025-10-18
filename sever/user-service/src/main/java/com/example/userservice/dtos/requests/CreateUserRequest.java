package com.example.userservice.dtos.requests;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String fullname;
    private String role;
    private String status;
    private MultipartFile avatar;
}
