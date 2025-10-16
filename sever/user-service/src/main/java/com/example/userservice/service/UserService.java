package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.requests.CreateUserRequest;
import com.example.userservice.dto.requests.UpdateUserRequest;
import com.example.userservice.dto.response.Response;
import com.example.userservice.dto.response.ResponseData;
import com.example.userservice.entity.User;
import com.example.userservice.entity.User.UserRole;
import com.example.userservice.entity.User.UserStatus;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.exception.OurException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.AuthenticatedUser;
import com.example.userservice.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    private final String PRIVATE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private final int PASSWORD_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
            CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.cloudinaryService = cloudinaryService;
    }

    private AuthenticatedUser requireAuthenticatedUser() {
        return SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new OurException("Unauthorized", 401));
    }

    private boolean isAdmin(AuthenticatedUser user) {
        return user.hasRole("ADMIN");
    }

    private void ensureAdmin() {
        AuthenticatedUser user = requireAuthenticatedUser();
        if (!isAdmin(user)) {
            throw new OurException("Forbidden", 403);
        }
    }

    private void ensureCanAccessUser(UUID userId) {
        AuthenticatedUser user = requireAuthenticatedUser();
        if (isAdmin(user)) {
            return;
        }
        if (!user.hasRole("USER") || !user.getUserId().equals(userId)) {
            throw new OurException("Forbidden", 403);
        }
    }

    public UserDto handleCreateUser(String username, String email, String password, String fullname, String role,
            String status) {
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

        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        user.setStatus(UserStatus.valueOf(status.toUpperCase()));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
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
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PRIVATE_CHARS.length());
            password.append(PRIVATE_CHARS.charAt(index));
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

    public Response createUser(CreateUserRequest createUserRequest) {
        Response response = new Response();

        try {
            ensureAdmin();

            UserDto savedUserDto = handleCreateUser(createUserRequest.getUsername(), createUserRequest.getEmail(),
                    createUserRequest.getPassword(), createUserRequest.getFullname(), createUserRequest.getRole(),
                    createUserRequest.getStatus());

            ResponseData data = new ResponseData();
            data.setUser(savedUserDto);

            response.setStatusCode(201);
            response.setMessage("User created successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public List<UserDto> handleGetAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public Response getAllUsers() {
        Response response = new Response();

        try {
            ensureAdmin();

            List<UserDto> userDtos = handleGetAllUsers();

            ResponseData data = new ResponseData();
            data.setUsers(userDtos);

            response.setMessage("Users retrieved successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public UserDto handleGetUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new OurException("User not found", 404));
        return userMapper.toDto(user);
    }

    public Response getUserById(UUID id) {
        Response response = new Response();

        try {
            ensureCanAccessUser(id);

            UserDto userDto = handleGetUserById(id);

            ResponseData data = new ResponseData();
            data.setUser(userDto);

            response.setMessage("User retrieved successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public UserDto handleUpdateUser(UUID id, String fullname, String role, String status, MultipartFile avatar) {
        User existingUser = userRepository.findById(id)
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

    public Response updateUser(UUID id, UpdateUserRequest request) {
        Response response = new Response();

        try {
            ensureCanAccessUser(id);

            UserDto updatedUserDto = handleUpdateUser(id, request.getFullname(), request.getRole(), request.getStatus(),
                    request.getAvatar());

            ResponseData data = new ResponseData();
            data.setUser(updatedUserDto);

            response.setMessage("User updated successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public boolean handleDeleteUser(UUID id) {
        UserDto user = handleGetUserById(id);

        String avatarPublicId = user.getAvatarPublicId();
        if (avatarPublicId != null && !avatarPublicId.isEmpty()) {
            cloudinaryService.deleteImage(avatarPublicId);
        }

        userRepository.deleteById(id);
        return true;
    }

    public Response deleteUser(UUID id) {
        Response response = new Response();

        try {
            ensureAdmin();

            handleDeleteUser(id);

            response.setMessage("User deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public UserDto handleFindByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }
}