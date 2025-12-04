package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        // Act
        request.setFullname("Full Name");
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        // Assert
        assertEquals("Full Name", request.getFullname());
        assertEquals(username, request.getUsername());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        RegisterRequest request = new RegisterRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        // DTO expects (fullname, username, email, password)
        RegisterRequest request = new RegisterRequest(
            "Full Name",
            "testuser",
            "test@example.com",
            "password123"
        );

        // Assert
        assertNotNull(request);
        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testBuilder() {
        // Act
        RegisterRequest request = RegisterRequest.builder()
            .fullname("Full Name")
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();

        // Assert
        assertNotNull(request);
        assertEquals("Full Name", request.getFullname());
        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        RegisterRequest request1 = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        RegisterRequest request2 = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        RegisterRequest request3 = RegisterRequest.builder()
                .username("different")
                .email("different@example.com")
                .password("different")
                .build();

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .fullname("Full Name")
                .username("testuser")
                .email("test@example.com")
                .build();

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }
}
