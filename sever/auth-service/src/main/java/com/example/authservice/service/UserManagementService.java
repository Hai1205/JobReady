package com.example.authservice.service;

import com.example.authservice.dto.OAuth2UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url:http://localhost:8083}")
    private String userServiceBaseUrl;

    public UserManagementService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Kiểm tra user đã tồn tại trong hệ thống hay chưa
     * 
     * @param email      Email của user
     * @param provider   OAuth2 provider (google, facebook, github)
     * @param providerId Provider-specific user ID
     * @return UserDto nếu tồn tại, null nếu không tồn tại
     */
    public Map<String, Object> checkUserExists(String email, String provider, String providerId) {
        try {
            String url = userServiceBaseUrl + "/users/oauth2/check";

            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("provider", provider);
            request.put("providerId", providerId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            logger.info("Checking if OAuth2 user exists: email={}, provider={}, providerId={}",
                    email, provider, providerId);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("User check successful for email: {}", email);
                return response.getBody();
            }

        } catch (HttpClientErrorException.NotFound e) {
            logger.info("User not found for email: {}, provider: {}", email, provider);
            return null;
        } catch (Exception e) {
            logger.error("Error checking user existence for email: {}", email, e);
            throw new RuntimeException("Failed to check user existence", e);
        }

        return null;
    }

    /**
     * Tạo user mới từ OAuth2 thông tin
     * 
     * @param oauth2UserDto Thông tin OAuth2 user
     * @return UserDto của user đã tạo
     */
    public Map<String, Object> createOAuth2User(OAuth2UserDto oauth2UserDto) {
        try {
            String url = userServiceBaseUrl + "/users/oauth2/create";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OAuth2UserDto> entity = new HttpEntity<>(oauth2UserDto, headers);

            logger.info("Creating OAuth2 user: email={}, provider={}",
                    oauth2UserDto.getEmail(), oauth2UserDto.getProvider());

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.CREATED ||
                    response.getStatusCode() == HttpStatus.OK) {
                logger.info("OAuth2 user created successfully for email: {}", oauth2UserDto.getEmail());
                return response.getBody();
            }

        } catch (Exception e) {
            logger.error("Error creating OAuth2 user for email: {}", oauth2UserDto.getEmail(), e);
            throw new RuntimeException("Failed to create OAuth2 user", e);
        }

        return null;
    }

    /**
     * Cập nhật thông tin user OAuth2 (avatar, name, etc.)
     * 
     * @param userId        ID của user
     * @param oauth2UserDto Thông tin cập nhật
     * @return UserDto đã cập nhật
     */
    public Map<String, Object> updateOAuth2User(Long userId, OAuth2UserDto oauth2UserDto) {
        try {
            String url = userServiceBaseUrl + "/users/oauth2/update/" + userId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OAuth2UserDto> entity = new HttpEntity<>(oauth2UserDto, headers);

            logger.info("Updating OAuth2 user: userId={}, email={}", userId, oauth2UserDto.getEmail());

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("OAuth2 user updated successfully for userId: {}", userId);
                return response.getBody();
            }

        } catch (Exception e) {
            logger.error("Error updating OAuth2 user for userId: {}", userId, e);
            throw new RuntimeException("Failed to update OAuth2 user", e);
        }

        return null;
    }

    /**
     * Xử lý OAuth2 user - check exists, create nếu chưa có, update nếu đã có
     * 
     * @param oauth2UserDto Thông tin OAuth2 user
     * @return UserDto final
     */
    public Map<String, Object> processOAuth2User(OAuth2UserDto oauth2UserDto) {
        logger.info("Processing OAuth2 user: email={}, provider={}",
                oauth2UserDto.getEmail(), oauth2UserDto.getProvider());

        // 1. Check if user exists
        Map<String, Object> existingUser = checkUserExists(
                oauth2UserDto.getEmail(),
                oauth2UserDto.getProvider(),
                oauth2UserDto.getProviderId());

        if (existingUser != null) {
            // 2. User exists - update info (avatar, name, etc.)
            Object userIdObj = existingUser.get("id");
            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                return updateOAuth2User(userId, oauth2UserDto);
            } else {
                logger.warn("Existing user found but no ID returned");
                return existingUser;
            }
        } else {
            // 3. User doesn't exist - create new
            return createOAuth2User(oauth2UserDto);
        }
    }
}