package com.example.authservice.service;

import com.example.authservice.dto.OAuth2UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit Tests cho OAuth2LoginService
 * Test business logic cho OAuth2 authentication processing
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "app.jwt.private-key-path=classpath:test-private-key.pem"
})
public class OAuth2LoginServiceTest {

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private OAuth2LoginService oauth2LoginService;

    private OAuth2User mockGoogleUser;
    private OAuth2User mockFacebookUser;
    private OAuth2User mockGithubUser;

    @BeforeEach
    void setUp() {
        // Mock Google OAuth2 User
        Map<String, Object> googleAttributes = new HashMap<>();
        googleAttributes.put("sub", "123456789");
        googleAttributes.put("email", "test@gmail.com");
        googleAttributes.put("name", "Test User");
        googleAttributes.put("picture", "https://example.com/avatar.jpg");

        mockGoogleUser = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                googleAttributes,
                "sub");

        // Mock Facebook OAuth2 User
        Map<String, Object> facebookAttributes = new HashMap<>();
        facebookAttributes.put("id", "987654321");
        facebookAttributes.put("email", "test@facebook.com");
        facebookAttributes.put("name", "Facebook User");

        mockFacebookUser = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                facebookAttributes,
                "id");

        // Mock GitHub OAuth2 User
        Map<String, Object> githubAttributes = new HashMap<>();
        githubAttributes.put("id", 12345);
        githubAttributes.put("login", "testuser");
        githubAttributes.put("email", "test@github.com");
        githubAttributes.put("name", "GitHub User");
        githubAttributes.put("avatar_url", "https://github.com/avatar.jpg");

        mockGithubUser = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                githubAttributes,
                "id");
    }

    @Test
    void testProcessOAuth2LoginSuccess() {
        // Mock successful user processing
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", 1L);
        userResponse.put("email", "test@gmail.com");
        userResponse.put("name", "Test User");

        when(userManagementService.processOAuth2User(any(OAuth2UserDto.class)))
                .thenReturn(userResponse);

        String jwtToken = oauth2LoginService.processOAuth2Login(mockGoogleUser, "google");

        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith("eyJ")); // JWT format check
    }

    @Test
    void testProcessOAuth2LoginWithNullUser() {
        assertThrows(RuntimeException.class, () -> {
            oauth2LoginService.processOAuth2Login(null, "google");
        });
    }

    @Test
    void testProcessOAuth2LoginWithNullProvider() {
        assertThrows(RuntimeException.class, () -> {
            oauth2LoginService.processOAuth2Login(mockGoogleUser, null);
        });
    }

    @Test
    void testProcessOAuth2LoginWithEmptyProvider() {
        assertThrows(RuntimeException.class, () -> {
            oauth2LoginService.processOAuth2Login(mockGoogleUser, "");
        });
    }

    @Test
    void testGoogleUserAttributeExtraction() {
        // Test Google user attributes are correctly parsed
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", 1L);

        when(userManagementService.processOAuth2User(any(OAuth2UserDto.class)))
                .thenAnswer(invocation -> {
                    OAuth2UserDto dto = invocation.getArgument(0);
                    assertEquals("test@gmail.com", dto.getEmail());
                    assertEquals("Test User", dto.getName());
                    assertEquals("google", dto.getProvider());
                    assertEquals("123456789", dto.getProviderId());
                    assertEquals("https://example.com/avatar.jpg", dto.getAvatarUrl());
                    return userResponse;
                });

        oauth2LoginService.processOAuth2Login(mockGoogleUser, "google");
    }

    @Test
    void testFacebookUserAttributeExtraction() {
        // Test Facebook user attributes are correctly parsed
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", 1L);

        when(userManagementService.processOAuth2User(any(OAuth2UserDto.class)))
                .thenAnswer(invocation -> {
                    OAuth2UserDto dto = invocation.getArgument(0);
                    assertEquals("test@facebook.com", dto.getEmail());
                    assertEquals("Facebook User", dto.getName());
                    assertEquals("facebook", dto.getProvider());
                    assertEquals("987654321", dto.getProviderId());
                    return userResponse;
                });

        oauth2LoginService.processOAuth2Login(mockFacebookUser, "facebook");
    }

    @Test
    void testGitHubUserAttributeExtraction() {
        // Test GitHub user attributes are correctly parsed
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", 1L);

        when(userManagementService.processOAuth2User(any(OAuth2UserDto.class)))
                .thenAnswer(invocation -> {
                    OAuth2UserDto dto = invocation.getArgument(0);
                    assertEquals("test@github.com", dto.getEmail());
                    assertEquals("GitHub User", dto.getName());
                    assertEquals("github", dto.getProvider());
                    assertEquals("12345", dto.getProviderId());
                    assertEquals("https://github.com/avatar.jpg", dto.getAvatarUrl());
                    return userResponse;
                });

        oauth2LoginService.processOAuth2Login(mockGithubUser, "github");
    }

    @Test
    void testUnsupportedProvider() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "test123");

        OAuth2User unsupportedUser = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id");

        assertThrows(RuntimeException.class, () -> {
            oauth2LoginService.processOAuth2Login(unsupportedUser, "unsupported");
        });
    }
}