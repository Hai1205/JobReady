package com.example.cvservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDto {
    private UUID id;
    private String fullname;
    private String email;
    private String phone;
    private String location;
    private String summary;
    private MultipartFile avatar;
    private String avatarUrl;
    private String avatarPublicId;
}
