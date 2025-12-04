package com.example.authservice.dtos;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        UserDto userDto = new UserDto();
        UUID id = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";
        String role = "USER";
        String status = "ACTIVE";
        LocalDateTime now = LocalDateTime.now();

        // Act
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setStatus(status);
        userDto.setFullname("Full Name");

        // Assert
        assertEquals(id, userDto.getId());
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
        assertEquals(role, userDto.getRole());
        assertEquals(status, userDto.getStatus());
        assertEquals("Full Name", userDto.getFullname());
    }

    @Test
    void testBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";

        // Act
        UserDto userDto = UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .role("USER")
                .status("ACTIVE")
                .build();

        // Assert
        assertNotNull(userDto);
        assertEquals(id, userDto.getId());
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        UserDto userDto = new UserDto();

        // Assert
        assertNotNull(userDto);
        assertNull(userDto.getId());
        assertNull(userDto.getUsername());
        assertNull(userDto.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        // Use the basic constructor present on the DTO (id, username, email, fullname)
        UserDto userDto = new UserDto(
            id,
            "testuser",
            "test@example.com",
            "Full Name"
        );

        // Assert
        assertNotNull(userDto);
        assertEquals(id, userDto.getId());
        assertEquals("testuser", userDto.getUsername());
        assertEquals("test@example.com", userDto.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserDto userDto1 = UserDto.builder()
                .id(id)
                .username("testuser")
                .email("test@example.com")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(id)
                .username("testuser")
                .email("test@example.com")
                .build();

        UserDto userDto3 = UserDto.builder()
                .id(UUID.randomUUID())
                .username("different")
                .email("different@example.com")
                .build();

        // Assert
        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertEquals(userDto1.hashCode(), userDto2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        // Act
        String toString = userDto.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }
}
