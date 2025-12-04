package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        String password = "newPass123";
        String confirm = "newPass123";

        // Act
        request.setPassword(password);
        request.setConfirmPassword(confirm);

        // Assert
        assertEquals(password, request.getPassword());
        assertEquals(confirm, request.getConfirmPassword());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ForgotPasswordRequest request = new ForgotPasswordRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getPassword());
        assertNull(request.getConfirmPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        ForgotPasswordRequest request = new ForgotPasswordRequest("newPass123", "newPass123");

        // Assert
        assertNotNull(request);
        assertEquals("newPass123", request.getPassword());
        assertEquals("newPass123", request.getConfirmPassword());
    }

    @Test
    void testBuilder() {
        // Act
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
            .password("newPass123")
            .confirmPassword("newPass123")
            .build();

        // Assert
        assertNotNull(request);
        assertEquals("newPass123", request.getPassword());
        assertEquals("newPass123", request.getConfirmPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        ForgotPasswordRequest request1 = new ForgotPasswordRequest("newPass123","newPass123");
        ForgotPasswordRequest request2 = new ForgotPasswordRequest("newPass123","newPass123");
        ForgotPasswordRequest request3 = new ForgotPasswordRequest("other","other");

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("newPass123","newPass123");

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("newPass123"));
    }
}
