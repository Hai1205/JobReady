package com.example.userservice.dtos.requests.user;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String fullname;
    private String phone;
    private String location;
    private String birth;
    private String summary;
    private MultipartFile avatar;
    private String role;
    private String status;
}
