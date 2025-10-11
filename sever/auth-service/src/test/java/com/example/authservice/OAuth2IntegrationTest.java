package com.example.authservice;

import com.example.authservice.controller.OAuth2Controller;
import com.example.authservice.service.OAuth2LoginService;
import com.example.authservice.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests cho OAuth2 Authentication
 * Test các flow OAuth2 login với different providers
 */
@WebMvcTest(OAuth2Controller.class)
@ActiveProfiles("test")
public class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuth2LoginService oauth2LoginService;

    @MockBean
    private UserManagementService userManagementService;

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
    @WithMockUser
    void testGoogleOAuth2Authorization() throws Exception {
        mockMvc.perform(get("/oauth2/authorize/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser
    void testFacebookOAuth2Authorization() throws Exception {
        mockMvc.perform(get("/oauth2/authorize/facebook"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/facebook"));
    }

    @Test
    @WithMockUser
    void testGitHubOAuth2Authorization() throws Exception {
        mockMvc.perform(get("/oauth2/authorize/github"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/github"));
    }

    @Test
    @WithMockUser
    void testUnsupportedProviderReturnsError() throws Exception {
        mockMvc.perform(get("/oauth2/authorize/unsupported"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsupported OAuth2 provider: unsupported"));
    }

    @Test
    @WithMockUser
    void testGoogleOAuth2CallbackSuccess() throws Exception {
        // Mock successful OAuth2 login processing
        String expectedJwtToken = "eyJhbGciOiJSUzI1NiJ9.test.token";
        when(oauth2LoginService.processOAuth2Login(any(OAuth2User.class), eq("google")))
                .thenReturn(expectedJwtToken);

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                mockGoogleUser,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "google");

        mockMvc.perform(get("/oauth2/callback/google")
                .sessionAttr("SPRING_SECURITY_CONTEXT", authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login-success?token=" + expectedJwtToken));
    }

    @Test
    @WithMockUser
    void testFacebookOAuth2CallbackSuccess() throws Exception {
        String expectedJwtToken = "eyJhbGciOiJSUzI1NiJ9.facebook.token";
        when(oauth2LoginService.processOAuth2Login(any(OAuth2User.class), eq("facebook")))
                .thenReturn(expectedJwtToken);

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                mockFacebookUser,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "facebook");

        mockMvc.perform(get("/oauth2/callback/facebook")
                .sessionAttr("SPRING_SECURITY_CONTEXT", authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login-success?token=" + expectedJwtToken));
    }

    @Test
    @WithMockUser
    void testGitHubOAuth2CallbackSuccess() throws Exception {
        String expectedJwtToken = "eyJhbGciOiJSUzI1NiJ9.github.token";
        when(oauth2LoginService.processOAuth2Login(any(OAuth2User.class), eq("github")))
                .thenReturn(expectedJwtToken);

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                mockGithubUser,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "github");

        mockMvc.perform(get("/oauth2/callback/github")
                .sessionAttr("SPRING_SECURITY_CONTEXT", authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login-success?token=" + expectedJwtToken));
    }

    @Test
    @WithMockUser
    void testOAuth2CallbackWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/oauth2/callback/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login-error?error=authentication_failed"));
    }

    @Test
    @WithMockUser
    void testOAuth2CallbackWithProcessingError() throws Exception {
        // Mock OAuth2 login processing failure
        when(oauth2LoginService.processOAuth2Login(any(OAuth2User.class), eq("google")))
                .thenThrow(new RuntimeException("User processing failed"));

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                mockGoogleUser,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "google");

        mockMvc.perform(get("/oauth2/callback/google")
                .sessionAttr("SPRING_SECURITY_CONTEXT", authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login-error?error=processing_failed"));
    }
}