package com.example.userservice.controllers;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.dtos.requests.CreateUserRequest;
import com.example.userservice.dtos.response.Response;
import com.example.userservice.services.apis.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
                org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(com.example.userservice.configs.SecurityConfig.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private com.example.userservice.securities.JwtTokenProvider jwtTokenProvider;

        @MockBean
        private com.example.userservice.securities.JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

        @MockBean
        private com.example.userservice.securities.JsonAccessDeniedHandler jsonAccessDeniedHandler;

        private ObjectMapper objectMapper;
        private Response successResponse;
        private UserDto mockUserDto;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();

                // Mock user DTO
                mockUserDto = new UserDto();
                mockUserDto.setId(UUID.randomUUID());
                mockUserDto.setEmail("test@example.com");
                mockUserDto.setUsername("testuser");
                mockUserDto.setFullname("Test User");
                mockUserDto.setRole("user");
                mockUserDto.setStatus("active");

                // Mock success response
                successResponse = new Response();
                successResponse.setStatusCode(200);
                successResponse.setMessage("Success");
                successResponse.setUser(mockUserDto);
        }

        @Test
        @WithMockUser(authorities = "admin")
        void testCreateUser_Success() throws Exception {
                // Arrange
                CreateUserRequest createRequest = new CreateUserRequest();
                createRequest.setUsername("newuser");
                createRequest.setEmail("newuser@example.com");
                createRequest.setPassword("password123");
                createRequest.setFullname("New User");
                String dataJson = objectMapper.writeValueAsString(createRequest);

                when(userService.createUser(anyString())).thenReturn(successResponse);

                MockMultipartFile dataPart = new MockMultipartFile(
                                "data", "", "application/json", dataJson.getBytes());

                // Act & Assert
                mockMvc.perform(multipart("/users")
                                .file(dataPart)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.message").value("Success"));

                verify(userService).createUser(anyString());
        }

        @Test
        @WithMockUser(authorities = "admin")
        void testGetAllUsers_Success() throws Exception {
                // Arrange
                Response response = new Response();
                response.setStatusCode(200);
                response.setUsers(Arrays.asList(mockUserDto));

                when(userService.getAllUsers()).thenReturn(response);

                // Act & Assert
                mockMvc.perform(get("/users")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.users").isArray());

                verify(userService).getAllUsers();
        }

        @Test
        @WithMockUser(authorities = "user")
        void testGetUserById_Success() throws Exception {
                // Arrange
                UUID userId = UUID.randomUUID();
                when(userService.getUserById(any(UUID.class))).thenReturn(successResponse);

                // Act & Assert
                mockMvc.perform(get("/users/" + userId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.user.username").value("testuser"));

                verify(userService).getUserById(any(UUID.class));
        }

        @Test
        @WithMockUser(authorities = "user")
        void testUpdateUser_Success() throws Exception {
                // Arrange
                UUID userId = UUID.randomUUID();
                String dataJson = "{\"fullname\":\"Updated Name\"}";

                when(userService.updateUser(any(UUID.class), anyString(), any()))
                                .thenReturn(successResponse);

                MockMultipartFile dataPart = new MockMultipartFile(
                                "data", "", "application/json", dataJson.getBytes());

                // Act & Assert
                mockMvc.perform(multipart("/users/" + userId)
                                .file(dataPart)
                                .with(request -> {
                                        request.setMethod("PATCH");
                                        return request;
                                })
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200));

                verify(userService).updateUser(any(UUID.class), anyString(), any());
        }

        @Test
        @WithMockUser(authorities = "user")
        void testUpdateUser_WithAvatar() throws Exception {
                // Arrange
                UUID userId = UUID.randomUUID();
                String dataJson = "{\"fullname\":\"Updated Name\"}";

                when(userService.updateUser(any(UUID.class), anyString(), any()))
                                .thenReturn(successResponse);

                MockMultipartFile dataPart = new MockMultipartFile(
                                "data", "", "application/json", dataJson.getBytes());
                MockMultipartFile avatarPart = new MockMultipartFile(
                                "avatar", "avatar.jpg", "image/jpeg", "image data".getBytes());

                // Act & Assert
                mockMvc.perform(multipart("/users/" + userId)
                                .file(dataPart)
                                .file(avatarPart)
                                .with(request -> {
                                        request.setMethod("PATCH");
                                        return request;
                                })
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200));

                verify(userService).updateUser(any(UUID.class), anyString(), any());
        }

        @Test
        @WithMockUser(authorities = "admin")
        void testDeleteUser_Success() throws Exception {
                // Arrange
                UUID userId = UUID.randomUUID();
                Response response = new Response();
                response.setStatusCode(200);
                response.setMessage("User deleted successfully");

                when(userService.deleteUser(any(UUID.class))).thenReturn(response);

                // Act & Assert
                mockMvc.perform(delete("/users/" + userId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200));

                verify(userService).deleteUser(any(UUID.class));
        }

        @Test
        @WithMockUser
        void testHealth_Success() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/users/health")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.message").value("User Service is running"));
        }
}
