package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        String current = "oldPass";
        String nw = "newPassword123";
        String confirm = "newPassword123";

        // Act
        request.setCurrentPassword(current);
        request.setNewPassword(nw);
        request.setConfirmPassword(confirm);

        // Assert
        assertEquals(current, request.getCurrentPassword());
        assertEquals(nw, request.getNewPassword());
        assertEquals(confirm, request.getConfirmPassword());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ChangePasswordRequest request = new ChangePasswordRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getCurrentPassword());
        assertNull(request.getNewPassword());
        assertNull(request.getConfirmPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPass",
            "newPassword123",
            "newPassword123"
        );

        // Assert
        assertNotNull(request);
        assertEquals("oldPass", request.getCurrentPassword());
        assertEquals("newPassword123", request.getNewPassword());
        assertEquals("newPassword123", request.getConfirmPassword());
    }

    @Test
    void testBuilder() {
        // Act
        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .currentPassword("oldPass")
            .newPassword("newPassword123")
            .confirmPassword("newPassword123")
            .build();

        // Assert
        assertNotNull(request);
        assertEquals("oldPass", request.getCurrentPassword());
        assertEquals("newPassword123", request.getNewPassword());
        assertEquals("newPassword123", request.getConfirmPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        ChangePasswordRequest request1 = ChangePasswordRequest.builder()
            .currentPassword("oldPass")
            .newPassword("newPassword123")
            .confirmPassword("newPassword123")
            .build();

        ChangePasswordRequest request2 = ChangePasswordRequest.builder()
            .currentPassword("oldPass")
            .newPassword("newPassword123")
            .confirmPassword("newPassword123")
            .build();

        ChangePasswordRequest request3 = ChangePasswordRequest.builder()
            .currentPassword("different")
            .newPassword("differentNew")
            .confirmPassword("differentNew")
            .build();

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .currentPassword("oldPass")
            .newPassword("newPassword123")
            .confirmPassword("newPassword123")
            .build();

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("oldPass"));
    }
}
