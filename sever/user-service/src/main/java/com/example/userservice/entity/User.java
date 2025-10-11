package com.example.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    // OAuth2 Provider Information
    private String oauthProvider; // google, facebook, github
    private String oauthProviderId; // Provider-specific user ID
    private String avatarUrl; // Profile picture URL
    private boolean isOAuthUser = false; // Flag to distinguish OAuth vs regular users

    public User() {
    }

    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isOAuthUser = false;
    }

    // Constructor for OAuth2 users
    public User(String username, String email, String firstName, String lastName,
            String oauthProvider, String oauthProviderId, String avatarUrl) {
        this.username = username;
        this.password = "OAUTH_USER"; // OAuth users don't have traditional passwords
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.avatarUrl = avatarUrl;
        this.isOAuthUser = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // OAuth2 related getters and setters
    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getOauthProviderId() {
        return oauthProviderId;
    }

    public void setOauthProviderId(String oauthProviderId) {
        this.oauthProviderId = oauthProviderId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isOAuthUser() {
        return isOAuthUser;
    }

    public void setOAuthUser(boolean isOAuthUser) {
        this.isOAuthUser = isOAuthUser;
    }
}