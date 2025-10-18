package com.example.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // OAuth2 related methods
    Optional<User> findByEmailAndOauthProvider(String email, String oauthProvider);

    Optional<User> findByOauthProviderAndOauthProviderId(String oauthProvider, String oauthProviderId);

    boolean existsByEmailAndOauthProvider(String email, String oauthProvider);
}