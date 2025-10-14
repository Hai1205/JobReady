package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    // OAuth2 Provider Information
    private String oauthProvider; // google, facebook, github
    private String oauthProviderId; // Provider-specific user ID
    private String avatarUrl; // Profile picture URL
    private boolean isOAuthUser = false; // Flag to distinguish OAuth vs regular users

    public User(String username, String password, String email, String fullname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.isOAuthUser = false;
    }

    // Constructor for OAuth2 users
    public User(String username, String email, String fullname, String lastName,
            String oauthProvider, String oauthProviderId, String avatarUrl) {
        this.username = username;
        this.password = "OAUTH_USER"; // OAuth users don't have traditional passwords
        this.email = email;
        this.fullname = fullname;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.isOAuthUser = true;
    }

    public enum UserRole {
        USER,
        ADMIN
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        PENDING
    }
}