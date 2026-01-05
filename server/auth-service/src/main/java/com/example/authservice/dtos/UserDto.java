package com.example.authservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor // Empty constructor for MapStruct
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String phone;
    private String location;
    private String birth;
    private String summary;
    private String status;
    private String role;

    // OAuth2 Provider Information
    private String oauthProvider;
    private String oauthProviderId;
    private String avatarUrl;
    private String avatarPublicId;
    private boolean isOAuthUser;

    // Plan Information
    private String planType;
    private LocalDateTime planExpiration;

    // Constructor with all fields for MapStruct
    @Builder
    public UserDto(UUID id, String username, String password, String email,
            String fullname, String phone, String location, String birth, String summary,
            String status, String role, String oauthProvider,
            String oauthProviderId, String avatarUrl, String avatarPublicId, boolean isOAuthUser,
            String planType, LocalDateTime planExpiration) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.location = location;
        this.birth = birth;
        this.summary = summary;
        this.status = status;
        this.role = role;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.isOAuthUser = isOAuthUser;
        this.planType = planType;
        this.planExpiration = planExpiration;
    }

    // Basic constructor
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