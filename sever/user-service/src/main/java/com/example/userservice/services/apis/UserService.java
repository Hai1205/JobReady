package com.example.userservice.services.apis;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.dtos.requests.*;
import com.example.userservice.dtos.response.Response;
import com.example.userservice.entities.*;
import com.example.userservice.entities.User.UserRole;
import com.example.userservice.entities.User.UserStatus;
import com.example.userservice.exceptions.OurException;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.securities.*;
import com.example.userservice.services.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;
    private final SecureRandom random = new SecureRandom();
    private final ObjectMapper objectMapper;

    @Value("${PRIVATE_CHARS}")
    private String privateChars;

    @Value("${PASSWORD_LENGTH}")
    private int passwordLength;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
            CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.cloudinaryService = cloudinaryService;
        this.objectMapper = new ObjectMapper();
    }

    public UserDto handleCreateUser(String username,
            String email,
            String password,
            String fullname,
            String role,
            String status,
            MultipartFile avatar) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        if (username.isEmpty()) {
            username = email.split("@")[0];
        }

        User user = new User(
                username,
                email,
                fullname);

        if (avatar != null && !avatar.isEmpty()) {
            var uploadResult = cloudinaryService.uploadImage(avatar);
            if (uploadResult.containsKey("error")) {
                throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
            }

            user.setAvatarUrl((String) uploadResult.get("url"));
            user.setAvatarPublicId((String) uploadResult.get("publicId"));
        }

        user.setPassword(passwordEncoder.encode(password));

        if (!role.isEmpty()) {
            user.setRole(UserRole.valueOf(role.toUpperCase()));
        }
        if (!status.isEmpty()) {
            user.setStatus(UserStatus.valueOf(status.toUpperCase()));
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto handleActivateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto handleAuthenticateUser(String email, String currentPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found", 404));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new OurException("Invalid credentials", 400);
        }

        return userMapper.toDto(user);
    }

    public String handleGenerateRandomPassword() {
        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            int index = random.nextInt(privateChars.length());
            password.append(privateChars.charAt(index));
        }
        return password.toString();
    }

    public String handleResetPasswordUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found", 404));

        String newPassword = handleGenerateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }

    public UserDto handleForgotPasswordUser(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found", 404));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto handleChangePasswordUser(String email, String currentPassword, String newPassword) {
        UserDto userDto = handleAuthenticateUser(email, currentPassword);

        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return userDto;
    }

    public Response createUser(String dataJson) {
        Response response = new Response();

        try {
            CreateUserRequest request = objectMapper.readValue(dataJson, CreateUserRequest.class);
            String username = request.getUsername();
            String email = request.getEmail();
            String password = request.getPassword();
            String fullname = request.getFullname();
            String role = request.getRole();
            String status = request.getStatus();
            MultipartFile avatar = request.getAvatar();

            UserDto savedUserDto = handleCreateUser(username, email, password, fullname, role, status, avatar);

            response.setStatusCode(201);
            response.setMessage("User created successfully");
            response.setUser(savedUserDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public List<UserDto> handleGetAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public Response getAllUsers() {
        Response response = new Response();

        try {
            List<UserDto> userDtos = handleGetAllUsers();

            response.setMessage("Users retrieved successfully");
            response.setUsers(userDtos);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public UserDto handleGetUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OurException("User not found", 404));
        return userMapper.toDto(user);
    }

    public Response getUserById(UUID userId) {
        Response response = new Response();

        try {
            UserDto userDto = handleGetUserById(userId);

            response.setMessage("User retrieved successfully");
            response.setUser(userDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public UserDto handleUpdateUser(UUID userId, String fullname, String role, String status, MultipartFile avatar) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser().orElse(null);
        boolean privilegedChangeAllowed = currentUser == null || currentUser.hasRole("ADMIN");

        // Handle avatar upload
        if (avatar != null && !avatar.isEmpty()) {
            // Delete old avatar if exists
            String oldAvatarPublicId = existingUser.getAvatarPublicId();
            if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                cloudinaryService.deleteImage(oldAvatarPublicId);
            }

            // Upload new avatar
            var uploadResult = cloudinaryService.uploadImage(avatar);
            if (uploadResult.containsKey("error")) {
                throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
            }

            existingUser.setAvatarUrl((String) uploadResult.get("url"));
            existingUser.setAvatarPublicId((String) uploadResult.get("publicId"));
        }

        // Update other fields
        if (fullname != null && !fullname.isEmpty() && !fullname.equals(existingUser.getFullname())) {
            existingUser.setFullname(fullname);
        }

        if (role != null && !role.isEmpty()) {
            if (!privilegedChangeAllowed) {
                throw new OurException("Forbidden", 403);
            }
            existingUser.setRole(UserRole.valueOf(role.toUpperCase()));
        }

        if (status != null && !status.isEmpty()) {
            if (!privilegedChangeAllowed) {
                throw new OurException("Forbidden", 403);
            }
            existingUser.setStatus(UserStatus.valueOf(status.toUpperCase()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    public Response updateUser(UUID userId, String dataJson) {
        Response response = new Response();

        try {
            UpdateUserRequest request = objectMapper.readValue(dataJson, UpdateUserRequest.class);
            String fullname = request.getFullname();
            String role = request.getRole();
            String status = request.getStatus();
            MultipartFile avatar = request.getAvatar();

            UserDto updatedUserDto = handleUpdateUser(userId, fullname, role, status, avatar);

            response.setMessage("User updated successfully");
            response.setUser(updatedUserDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public boolean handleDeleteUser(UUID userId) {
        UserDto user = handleGetUserById(userId);

        String avatarPublicId = user.getAvatarPublicId();
        if (avatarPublicId != null && !avatarPublicId.isEmpty()) {
            cloudinaryService.deleteImage(avatarPublicId);
        }

        userRepository.deleteById(userId);
        return true;
    }

    public Response deleteUser(UUID userId) {
        Response response = new Response();

        try {
            handleDeleteUser(userId);

            response.setMessage("User deleted successfully");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public UserDto handleFindByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    public UserDto handleFindById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElse(null);
    }
}