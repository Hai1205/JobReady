package com.example.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String firstName;
    private String lastName;
    private String status;
    private String role;

    // OAuth2 Provider Information
    private String oauthProvider;
    private String oauthProviderId;
    private String avatarUrl;
    private boolean isOAuthUser = false;

    public UserDto(UUID id, String username, String email, String fullname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.isOAuthUser = false;
    }

    // Constructor for OAuth2 users
    public UserDto(UUID id, String username, String email, String fullname,
            String oauthProvider, String oauthProviderId, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.isOAuthUser = true;
    }
}