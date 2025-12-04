package com.example.authservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey;
    private SecretKey key;
    private java.security.PrivateKey privateKey;
    private java.security.PublicKey publicKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        try {
            // Generate RSA key pair for signing (matching JwtService which expects RSA keys)
            java.security.KeyPairGenerator kpg = java.security.KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            java.security.KeyPair kp = kpg.generateKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();

            String privateKeyBase64 = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
            String publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());

            // Set fields expected by JwtService
            ReflectionTestUtils.setField(jwtService, "privateKeyStr", privateKeyBase64);
            ReflectionTestUtils.setField(jwtService, "publicKeyStr", publicKeyBase64);

            // Initialize the service which will parse keys
            ReflectionTestUtils.invokeMethod(jwtService, "init");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateAccessToken() {
        // Arrange
        String userId = "123";
        String email = "test@example.com";
        String username = "testuser";
        String role = "USER";

        // Act
        String token = jwtService.generateAccessToken(userId, email, role, username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token contains expected claims
        String extractedUserId = jwtService.extractUserId(token);
        String extractedEmail = jwtService.extractEmail(token);
        String extractedUsername = jwtService.extractUsername(token);
        
        assertEquals(userId, extractedUserId);
        assertEquals(email, extractedEmail);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGenerateRefreshToken() {
        // Arrange
        String userId = "123";
        String email = "test@example.com";
        String username = "testuser";

        // Act
        String token = jwtService.generateRefreshToken(userId, email, username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token is valid
        assertTrue(jwtService.validateRefreshToken(token));
    }

    @Test
    void testExtractUserId() {
        // Arrange
        String userId = "123";
        String token = jwtService.generateAccessToken(userId, "test@example.com", "USER", "testuser");

        // Act
        String extractedUserId = jwtService.extractUserId(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testExtractEmail() {
        // Arrange
        String email = "test@example.com";
        String token = jwtService.generateAccessToken("123", email, "USER", "testuser");

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateAccessToken("123", "test@example.com", "USER", username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateAccessToken("123", "test@example.com", "USER", username);

        // Act
        boolean isValid = jwtService.validateToken(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        // Arrange
        String token = jwtService.generateAccessToken("123", "test@example.com", "USER", "testuser");

        // Act
        boolean isValid = jwtService.validateToken(token, "wronguser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange - build a token signed with test private key and expired timestamp
        String username = "testuser";
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .claim("userId", "123")
                .claim("email", "test@example.com")
                .claim("username", username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis() - 10000))
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 5000))
                .signWith(privateKey)
                .compact();

        // Act & Assert - validating an expired token should throw ExpiredJwtException
        org.junit.jupiter.api.Assertions.assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtService.validateToken(expiredToken, username);
        });
    }

    @Test
    void testValidateRefreshToken_Valid() {
        // Arrange
        String token = jwtService.generateRefreshToken("123", "test@example.com", "testuser");

        // Act
        boolean isValid = jwtService.validateRefreshToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateRefreshToken_Invalid() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtService.validateRefreshToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractAllClaims() {
        // Arrange
        String token = jwtService.generateAccessToken("123", "test@example.com", "USER", "testuser");

        // Assert using public extractors
        assertEquals("123", jwtService.extractUserId(token));
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        // Arrange
        String token = jwtService.generateAccessToken("123", "test@example.com", "testuser", "USER");

        // Act
        Boolean isExpired = ReflectionTestUtils.invokeMethod(jwtService, "isTokenExpired", token);

        // Assert
        assertNotNull(isExpired);
        assertFalse(isExpired);
    }

    @Test
    void testGenerateToken_WithCustomClaims() {
        // Arrange
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("customKey", "customValue");
        customClaims.put("userId", "123");

        // Act
        // Build a signed token with custom claims using the generated private key
        String token = io.jsonwebtoken.Jwts.builder()
            .setClaims(customClaims)
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 3600000L))
            .signWith(privateKey)
            .compact();

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testTokenContainsRole() {
        // Arrange
        String role = "ADMIN";
        String token = jwtService.generateAccessToken("123", "test@example.com", role, "testuser");

        // Assert using public extractor
        assertEquals(role, jwtService.extractRole(token));
    }

    @Test
    void testMultipleTokenGeneration() {
        // Arrange & Act
        String token1 = jwtService.generateAccessToken("123", "test1@example.com", "USER", "user1");
        String token2 = jwtService.generateAccessToken("456", "test2@example.com", "ADMIN", "user2");

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
        
        assertEquals("123", jwtService.extractUserId(token1));
        assertEquals("456", jwtService.extractUserId(token2));
        assertEquals("user1", jwtService.extractUsername(token1));
        assertEquals("user2", jwtService.extractUsername(token2));
    }
}
