package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String identifier = "testuser";
        String password = "password123";

        // Act
        loginRequest.setIdentifier(identifier);
        loginRequest.setPassword(password);

        // Assert
        assertEquals(identifier, loginRequest.getIdentifier());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        LoginRequest loginRequest = new LoginRequest();

        // Assert
        assertNotNull(loginRequest);
        assertNull(loginRequest.getIdentifier());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        // Assert
        assertNotNull(loginRequest);
        assertEquals("testuser", loginRequest.getIdentifier());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testBuilder() {
        // Act
        LoginRequest loginRequest = LoginRequest.builder()
                .identifier("testuser")
                .password("password123")
                .build();

        // Assert
        assertNotNull(loginRequest);
        assertEquals("testuser", loginRequest.getIdentifier());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        LoginRequest request1 = new LoginRequest("testuser", "password123");
        LoginRequest request2 = new LoginRequest("testuser", "password123");
        LoginRequest request3 = new LoginRequest("different", "different");

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        // Act
        String toString = loginRequest.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
    }
}
