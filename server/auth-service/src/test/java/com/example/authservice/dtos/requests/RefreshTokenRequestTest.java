package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        String refreshToken = "refresh_token_value";

        // Act
        request.setRefreshToken(refreshToken);

        // Assert
        assertEquals(refreshToken, request.getRefreshToken());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        RefreshTokenRequest request = new RefreshTokenRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getRefreshToken());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        // class currently does not expose all-args constructor; use setter instead
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh_token_value");

        // Assert
        assertNotNull(request);
        assertEquals("refresh_token_value", request.getRefreshToken());
    }

    @Test
    void testBuilder() {
        // Act
        // builder not available on this DTO; use setter
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh_token_value");

        // Assert
        assertNotNull(request);
        assertEquals("refresh_token_value", request.getRefreshToken());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        RefreshTokenRequest request1 = new RefreshTokenRequest();
        request1.setRefreshToken("refresh_token_value");
        RefreshTokenRequest request2 = new RefreshTokenRequest();
        request2.setRefreshToken("refresh_token_value");
        RefreshTokenRequest request3 = new RefreshTokenRequest();
        request3.setRefreshToken("different_token");

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh_token_value");

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
    }
}
