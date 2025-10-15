package com.example.userservice.dto.requests;

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
    private String avatar;
}
