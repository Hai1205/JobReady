package com.example.userservice.service;

import com.example.userservice.dto.OAuth2UserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Tìm user OAuth2 theo email, provider và providerId
     */
    public UserDto findOAuth2User(String email, String provider, String providerId) {
        logger.debug("Finding OAuth2 user: email={}, provider={}, providerId={}", email, provider, providerId);

        // Tìm theo email và provider trước
        Optional<User> userByEmailAndProvider = userRepository.findByEmailAndOauthProvider(email, provider);
        if (userByEmailAndProvider.isPresent()) {
            return convertToDto(userByEmailAndProvider.get());
        }

        // Nếu không tìm thấy, thử tìm theo email thôi (trường hợp user có thể đã tồn
        // tại nhưng chưa link provider)
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            // Kiểm tra nếu user chưa có OAuth provider, có thể link
            if (user.getOauthProvider() == null || user.getOauthProvider().isEmpty()) {
                logger.info("Found existing user by email, can be linked to OAuth provider");
                return convertToDto(user);
            }
        }

        return null;
    }

    /**
     * Tìm user theo email và provider
     */
    public UserDto findByEmailAndProvider(String email, String provider) {
        logger.debug("Finding user by email and provider: email={}, provider={}", email, provider);

        Optional<User> user = userRepository.findByEmailAndOauthProvider(email, provider);
        return user.map(this::convertToDto).orElse(null);
    }

    /**
     * Tạo user mới từ OAuth2
     */
    public UserDto createOAuth2User(OAuth2UserDto oauth2UserDto) {
        logger.info("Creating OAuth2 user: email={}, provider={}", oauth2UserDto.getEmail(),
                oauth2UserDto.getProvider());

        // Kiểm tra user đã tồn tại chưa
        if (userRepository.existsByEmail(oauth2UserDto.getEmail())) {
            // User đã tồn tại, cần link với OAuth provider
            Optional<User> existingUser = userRepository.findByEmail(oauth2UserDto.getEmail());
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                return linkOAuth2Provider(user, oauth2UserDto);
            }
        }

        // Tạo username unique từ email hoặc name
        String username = generateUniqueUsername(oauth2UserDto);

        // Tạo user mới
        User newUser = new User(
                username,
                oauth2UserDto.getEmail(),
                oauth2UserDto.getFirstName() != null ? oauth2UserDto.getFirstName()
                        : extractFirstName(oauth2UserDto.getName()),
                oauth2UserDto.getLastName() != null ? oauth2UserDto.getLastName()
                        : extractLastName(oauth2UserDto.getName()),
                oauth2UserDto.getProvider(),
                oauth2UserDto.getProviderId(),
                oauth2UserDto.getAvatarUrl());

        User savedUser = userRepository.save(newUser);
        logger.info("OAuth2 user created successfully with ID: {}", savedUser.getId());

        return convertToDto(savedUser);
    }

    /**
     * Cập nhật user OAuth2
     */
    public UserDto updateOAuth2User(Long userId, OAuth2UserDto oauth2UserDto) {
        logger.info("Updating OAuth2 user: userId={}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Cập nhật thông tin
        if (oauth2UserDto.getFirstName() != null) {
            existingUser.setFirstName(oauth2UserDto.getFirstName());
        }
        if (oauth2UserDto.getLastName() != null) {
            existingUser.setLastName(oauth2UserDto.getLastName());
        }
        if (oauth2UserDto.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(oauth2UserDto.getAvatarUrl());
        }

        // Cập nhật provider info nếu chưa có
        if (existingUser.getOauthProvider() == null) {
            existingUser.setOauthProvider(oauth2UserDto.getProvider());
            existingUser.setOauthProviderId(oauth2UserDto.getProviderId());
            existingUser.setOAuthUser(true);
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("OAuth2 user updated successfully: userId={}", userId);

        return convertToDto(updatedUser);
    }

    /**
     * Link existing user với OAuth2 provider
     */
    private UserDto linkOAuth2Provider(User existingUser, OAuth2UserDto oauth2UserDto) {
        logger.info("Linking existing user with OAuth2 provider: userId={}, provider={}",
                existingUser.getId(), oauth2UserDto.getProvider());

        existingUser.setOauthProvider(oauth2UserDto.getProvider());
        existingUser.setOauthProviderId(oauth2UserDto.getProviderId());
        existingUser.setAvatarUrl(oauth2UserDto.getAvatarUrl());
        existingUser.setOAuthUser(true);

        // Cập nhật name nếu chưa có
        if (existingUser.getFirstName() == null && oauth2UserDto.getFirstName() != null) {
            existingUser.setFirstName(oauth2UserDto.getFirstName());
        }
        if (existingUser.getLastName() == null && oauth2UserDto.getLastName() != null) {
            existingUser.setLastName(oauth2UserDto.getLastName());
        }

        User savedUser = userRepository.save(existingUser);
        return convertToDto(savedUser);
    }

    /**
     * Tạo username unique
     */
    private String generateUniqueUsername(OAuth2UserDto oauth2UserDto) {
        String baseUsername;

        if (oauth2UserDto.getUsername() != null && !oauth2UserDto.getUsername().isEmpty()) {
            baseUsername = oauth2UserDto.getUsername();
        } else {
            // Extract từ email
            String emailPrefix = oauth2UserDto.getEmail().split("@")[0];
            baseUsername = emailPrefix.replaceAll("[^a-zA-Z0-9]", "");
        }

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    /**
     * Extract first name từ full name
     */
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty())
            return null;

        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    /**
     * Extract last name từ full name
     */
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty())
            return null;

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
        return null;
    }

    /**
     * Convert User entity to UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName());

        // Set OAuth2 related fields
        dto.setOauthProvider(user.getOauthProvider());
        dto.setOauthProviderId(user.getOauthProviderId());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setOAuthUser(user.isOAuthUser());

        return dto;
    }
}