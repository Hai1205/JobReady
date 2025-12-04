package com.example.authservice.dtos.responses;

import com.example.authservice.dtos.UserDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        Response response = new Response();
        int statusCode = 200;
        String message = "Success";
        UserDto user = new UserDto();
        user.setUsername("testuser");
        List<UserDto> users = Arrays.asList(user);
        String token = "access_token";
        Pagination pagination = new Pagination();

        // Act
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setUser(user);
        response.setUsers(users);
        response.setToken(token);
        response.setAdditionalData(null);

        // Assert
        assertEquals(statusCode, response.getStatusCode());
        assertEquals(message, response.getMessage());
        assertEquals(user, response.getUser());
        assertEquals(users, response.getUsers());
        assertEquals(token, response.getToken());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Response response = new Response();

        // Assert
        assertNotNull(response);
        // default constructor sets statusCode to 200
        assertEquals(200, response.getStatusCode());
        assertNull(response.getMessage());
        assertNull(response.getUser());
    }

    @Test
    void testBuilder() {
        // Arrange
        UserDto user = new UserDto();
        user.setUsername("testuser");

        // Act
        Response response = Response.builder()
            .statusCode(200)
            .message("Success")
            .user(user)
            .token("access_token")
            .build();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Success", response.getMessage());
        assertEquals(user, response.getUser());
        assertEquals("access_token", response.getToken());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Response response1 = Response.builder()
                .statusCode(200)
                .message("Success")
                .build();

        Response response2 = Response.builder()
                .statusCode(200)
                .message("Success")
                .build();

        Response response3 = Response.builder()
                .statusCode(404)
                .message("Not Found")
                .build();

        // Assert
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Response response = Response.builder()
                .statusCode(200)
                .message("Success")
                .build();

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("Success"));
    }

    @Test
    void testWithUsersList() {
        // Arrange
        UserDto user1 = new UserDto();
        user1.setUsername("user1");
        UserDto user2 = new UserDto();
        user2.setUsername("user2");
        List<UserDto> users = Arrays.asList(user1, user2);

        // Act
        Response response = Response.builder()
            .users(users)
            .build();

        // Assert
        assertNotNull(response.getUsers());
        assertEquals(2, response.getUsers().size());
        assertEquals("user1", response.getUsers().get(0).getUsername());
        assertEquals("user2", response.getUsers().get(1).getUsername());
    }

    @Test
    void testWithPagination() {
        // Arrange
        // Response does not expose pagination; instead test additionalData usage
        Pagination pagination = Pagination.builder()
            .currentPage(1)
            .totalPages(10)
            .totalItems(100L)
            .pageSize(10)
            .build();

        // Act - put pagination into additionalData map
        Response response = Response.builder()
            .additionalData(java.util.Map.of("pagination", pagination))
            .build();

        // Assert
        assertNotNull(response.getAdditionalData());
        assertTrue(response.getAdditionalData().containsKey("pagination"));
    }
}
