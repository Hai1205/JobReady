package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.requests.ChangePasswordRequest;
import com.example.userservice.dto.requests.ChangeStatusRequest;
import com.example.userservice.dto.requests.ResetPasswordRequest;
import com.example.userservice.dto.response.Data;
import com.example.userservice.dto.response.Response;
import com.example.userservice.entity.User;
import com.example.userservice.exception.AccountNotVerifiedException;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

    public Response getUserById(UUID id) {
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
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid user ID format");
            System.out.println(e.getMessage());
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
            logger.error("Error finding user by username: {}", username, e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            logger.error("Error finding user by username: {}", username, e);
        }

        return response;
    }

    /**
     * Tìm kiếm người dùng theo email cho RabbitMQ
     * 
     * @param email Email của người dùng
     * @return User đối tượng nếu tìm thấy, null nếu không
     */
    public User findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    /**
     * Thay đổi mật khẩu cho người dùng
     * 
     * @param userId          ID của người dùng
     * @param currentPassword Mật khẩu hiện tại
     * @param newPassword     Mật khẩu mới
     * @return User đã cập nhật
     */
    @Transactional
    public User changePassword(ChangePasswordRequest request) {
        logger.info("Changing password for user ID: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.getUserId()));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    /**
     * Đặt lại mật khẩu cho người dùng
     * 
     * @param userId      ID của người dùng
     * @param newPassword Mật khẩu mới
     * @return User đã cập nhật
     */
    @Transactional
    public User resetPassword(ResetPasswordRequest request) {
        logger.info("Resetting password for user ID: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.getUserId()));

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    /**
     * Đặt lại mật khẩu bằng email
     * 
     * @param email       Email của người dùng
     * @param newPassword Mật khẩu mới
     * @return User đã cập nhật
     */
    @Transactional
    public User resetPasswordByEmail(String email, String newPassword) {
        logger.info("Resetting password for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Thay đổi trạng thái người dùng (kích hoạt/vô hiệu hóa)
     * 
     * @param userId ID của người dùng
     * @param status Trạng thái mới (true: kích hoạt, false: vô hiệu hóa)
     * @return User đã cập nhật
     */
    @Transactional
    public User changeStatus(ChangeStatusRequest request) {
        logger.info("Changing status for user ID: {} to {}", request.getUserId(), request.getStatus());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.getUserId()));

        // Cập nhật trạng thái
        boolean status = "enable".equalsIgnoreCase(request.getStatus());
        if (status) {
            user.setStatus(User.UserStatus.ACTIVE);
        } else {
            user.setStatus(User.UserStatus.INACTIVE);
        }

        return userRepository.save(user);
    }

    public Response updateUser(UUID id, UserDto userDto) {
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
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid user ID format");
            System.out.println(e.getMessage());
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

    public Response deleteUser(UUID id) {
        Response response = new Response();

        try {
            UUID uuid = id;
            if (!userRepository.existsById(uuid)) {
                throw new RuntimeException("User not found");
            }

            userRepository.deleteById(uuid);

            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid user ID format");
            System.out.println(e.getMessage());
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

    /**
     * Authenticate a user by email and password
     * This method is specifically for RabbitMQ authentication requests
     * 
     * @param email    User email
     * @param password User password
     * @return UserDto if authenticated, null otherwise
     */
    public UserDto authenticateUserByEmail(String email, String password) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the account is pending verification
            if (user.getStatus() == User.UserStatus.PENDING) {
                throw new AccountNotVerifiedException(
                        "User account is not verified. Please verify your account first.");
            }

            // Verify password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            // Return the user data if authentication is successful
            return convertToDto(user);
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }
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
                user.getId() != null ? user.getId() : null,
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