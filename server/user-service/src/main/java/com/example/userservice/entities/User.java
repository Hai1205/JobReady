package com.example.userservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor // Empty constructor for MapStruct
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // private UUID planId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private String fullname;
    private String phone;
    private String location;
    private String birth;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.pending;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType = PlanType.free;

    private LocalDateTime planExpiration;

    // OAuth2 Provider Information
    private String oauthProvider; // google, facebook, github
    private String oauthProviderId; // Provider-specific user ID
    private String avatarUrl; // Profile picture URL
    private String avatarPublicId; // Cloudinary public ID for avatar deletion

    // Audit fields
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Full constructor for MapStruct
    @Builder
    public User(UUID id, String username, String password, String email, String fullname,
            String phone, String location, String birth, String summary,
            UserRole role, UserStatus status, String oauthProvider, String oauthProviderId,
            String avatarUrl, String avatarPublicId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.location = location;
        this.birth = birth;
        this.summary = summary;
        this.role = role;
        this.status = status;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Basic constructor
    public User(String username, String email, String fullname) {
        this.username = username;
        this.email = email;
        this.fullname = fullname;
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

    public enum PlanType {
        free,
        pro,
        ultra
    }
}
