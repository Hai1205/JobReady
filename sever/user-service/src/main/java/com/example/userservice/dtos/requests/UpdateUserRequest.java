package com.example.userservice.dtos.requests;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String fullname;
    private MultipartFile avatar;
    private String role;
    private String status;
}
