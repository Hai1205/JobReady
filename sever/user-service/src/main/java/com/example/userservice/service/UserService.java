package com.example.userservice.service;

import com.example.userservice.dto.Data;
import com.example.userservice.dto.Response;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exception.AccountNotVerifiedException;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Response createUser(UserDto userDto) {
        Response response = new Response();

        try {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            User user = new User(
                    userDto.getUsername(),
                    passwordEncoder.encode(userDto.getPassword()), // Encoding password
                    userDto.getEmail(),
                    userDto.getFullname());

            // Ensure status is set to PENDING for new users
            user.setStatus(User.UserStatus.PENDING);

            User savedUser = userRepository.save(user);
            UserDto savedUserDto = convertToDto(savedUser);

            Data data = new Data();
            data.setUser(savedUserDto);

            response.setStatusCode(201);
            response.setMessage("User created successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getAllUsers() {
        Response response = new Response();

        try {
            List<UserDto> userDtos = userRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            Data data = new Data();
            data.setUsers(userDtos);

            response.setStatusCode(200);
            response.setMessage("Users retrieved successfully");
            response.setData(data);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getUserById(Long id) {
        Response response = new Response();

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDto userDto = convertToDto(user);

            Data data = new Data();
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("User retrieved successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getUserByUsername(String username) {
        Response response = new Response();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDto userDto = convertToDto(user);

            Data data = new Data();
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("User retrieved successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response updateUser(Long id, UserDto userDto) {
        Response response = new Response();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existingUser.setEmail(userDto.getEmail());
            existingUser.setFullname(userDto.getFullname());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            UserDto updatedUserDto = convertToDto(updatedUser);

            Data data = new Data();
            data.setUser(updatedUserDto);

            response.setStatusCode(200);
            response.setMessage("User updated successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response deleteUser(Long id) {
        Response response = new Response();

        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found");
            }

            userRepository.deleteById(id);

            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response authenticateUser(String username, String password) {
        Response response = new Response();

        try {
            boolean isAuthenticated = userRepository.findByUsername(username)
                    .map(user -> {
                        if (user.getStatus() == User.UserStatus.PENDING) {
                            throw new AccountNotVerifiedException(
                                    "User account is not verified. Please verify your account first.");
                        }
                        return passwordEncoder.matches(password, user.getPassword());
                    })
                    .orElse(false);

            if (!isAuthenticated) {
                throw new RuntimeException("Invalid username or password");
            }

            User user = userRepository.findByUsername(username).get(); // We know it exists at this point
            UserDto userDto = convertToDto(user);

            Data data = new Data();
            data.setUser(userDto);
            data.setAuthenticated(true);

            response.setStatusCode(200);
            response.setMessage("User authenticated successfully");
            response.setData(data);
        } catch (AccountNotVerifiedException e) {
            response.setStatusCode(403);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            response.setStatusCode(401);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response activateUser(String username) {
        Response response = new Response();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getStatus() == User.UserStatus.PENDING) {
                user.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(user);
            }

            UserDto userDto = convertToDto(user);

            Data data = new Data();
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("User activated successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getUserStatus(String username) {
        Response response = new Response();

        try {
            User.UserStatus status = userRepository.findByUsername(username)
                    .map(User::getStatus)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Data data = new Data();
            data.setStatus(status.toString());

            response.setStatusCode(200);
            response.setMessage("User status retrieved successfully");
            response.setData(data);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname());

        // Set OAuth2 related fields
        dto.setOauthProvider(user.getOauthProvider());
        dto.setOauthProviderId(user.getOauthProviderId());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setOAuthUser(user.isOAuthUser());

        // Add status and role to DTO
        dto.setStatus(user.getStatus().toString());
        dto.setRole(user.getRole().toString());

        return dto;
    }
}