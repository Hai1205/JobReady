package com.example.userservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor // Empty constructor for MapStruct
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
    private UserRole role = UserRole.user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.pending;

    // OAuth2 Provider Information
    private String oauthProvider; // google, facebook, github
    private String oauthProviderId; // Provider-specific user ID
    private String avatarUrl; // Profile picture URL
    private String avatarPublicId; // Cloudinary public ID for avatar deletion
    private boolean isOAuthUser = false; // Flag to distinguish OAuth vs regular users

    // Full constructor for MapStruct
    @Builder
    public User(UUID id, String username, String password, String email, String fullname,
            UserRole role, UserStatus status, String oauthProvider, String oauthProviderId,
            String avatarUrl, String avatarPublicId, boolean isOAuthUser) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
        this.status = status;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.isOAuthUser = isOAuthUser;
    }

    // Basic constructor
    public User(String username, String email, String fullname) {
        this.username = username;
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
        user,
        admin
    }

    public enum UserStatus {
        active,
        banned,
        pending
    }
}
