package com.example.userservice.services.apis;

import com.example.userservice.dtos.UserDto;
import com.example.userservice.dtos.OAuth2UserDto;
import com.example.userservice.dtos.requests.*;
import com.example.userservice.dtos.response.Response;
import com.example.userservice.entities.*;
import com.example.userservice.entities.User.UserRole;
import com.example.userservice.entities.User.UserStatus;
import com.example.userservice.exceptions.OurException;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.repositories.UserRepository;
import com.example.cloudinarycommon.CloudinaryService;
import com.example.securitycommon.utils.SecurityUtils;
import com.example.securitycommon.models.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserApi extends BaseApi {

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

    public UserApi(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
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
        try {
            System.out.println("Stored password hash: " + password);
            logger.info("Creating user with email: {}", email);

            // Basic validation
            if (email == null || email.isEmpty()) {
                logger.warn("Attempted to create user with empty email");
                throw new OurException("Email is required", 400);
            }

            if (userRepository.existsByEmail(email)) {
                logger.warn("Attempted to create user with existing email: {}", email);
                throw new OurException("Email already exists", 400);
            }

            // Ensure username fallback to email prefix when not provided
            if (username == null || username.isEmpty()) {
                try {
                    username = email.split("@")[0];
                } catch (Exception ex) {
                    username = "user" + UUID.randomUUID().toString().substring(0, 8);
                }
            }

            // If no password provided, generate a secure random one
            if (password == null || password.isEmpty()) {
                password = handleGenerateRandomPassword();
                logger.info("No password provided for {} - generated a random password", email);
            }

            User user = new User(
                    username,
                    email,
                    fullname);

            if (avatar != null && !avatar.isEmpty()) {
                logger.debug("Uploading avatar for user: {}", email);
                var uploadResult = cloudinaryService.uploadImage(avatar);
                if (uploadResult.containsKey("error")) {
                    logger.error("Failed to upload avatar for user {}: {}", email, uploadResult.get("error"));
                    throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
                }

                user.setAvatarUrl((String) uploadResult.get("url"));
                user.setAvatarPublicId((String) uploadResult.get("publicId"));
            }

            user.setPassword(passwordEncoder.encode(password));

            if (role != null && !role.isEmpty()) {
                user.setRole(UserRole.valueOf(role));
            }
            if (status != null && !status.isEmpty()) {
                user.setStatus(UserStatus.valueOf(status));
            }

            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {}", savedUser.getId());
            return userMapper.toDto(savedUser);
        } catch (OurException e) {
            logger.error("Error in handleCreateUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleCreateUser: {}", e.getMessage(), e);
            throw new OurException("Failed to create user", 500);
        }
    }

    public UserDto handleActivateUser(String email) {
        try {
            logger.info("Activating user with email: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setStatus(UserStatus.active);
            userRepository.save(user);
            logger.info("User activated successfully: {}", email);
            return userMapper.toDto(user);
        } catch (OurException e) {
            logger.error("Error in handleActivateUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleActivateUser: {}", e.getMessage(), e);
            throw new OurException("Failed to activate user", 500);
        }
    }

    private boolean handleIsValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    public UserDto handleAuthenticateUser(String identifier, String currentPassword) {
        try {
            logger.debug("Authenticating user: {}", identifier);

            User user = null;
            boolean isEmailValid = handleIsValidEmail(identifier);
            if (isEmailValid) {
                user = userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new OurException("User not found", 404));
            } else {
                user = userRepository.findByUsername(identifier)
                        .orElseThrow(() -> new OurException("User not found", 404));
            }

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.warn("Invalid password attempt for user: {}", identifier);
                throw new OurException("Invalid credentials", 400);
            }

            logger.debug("User authenticated successfully: {}", identifier);
            return userMapper.toDto(user);
        } catch (OurException e) {
            logger.error("Error in handleAuthenticateUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleAuthenticateUser: {}", e.getMessage(), e);
            throw new OurException("Failed to authenticate user", 500);
        }
    }

    public String handleGenerateRandomPassword() {
        try {
            StringBuilder password = new StringBuilder(passwordLength);
            for (int i = 0; i < passwordLength; i++) {
                int index = random.nextInt(privateChars.length());
                password.append(privateChars.charAt(index));
            }
            return password.toString();
        } catch (Exception e) {
            logger.error("Error in handleGenerateRandomPassword: {}", e.getMessage(), e);
            throw new OurException("Failed to generate random password", 500);
        }
    }

    public String handleResetPasswordUser(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found", 404));

            String newPassword = handleGenerateRandomPassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return newPassword;
        } catch (OurException e) {
            logger.error("Error in handleResetPasswordUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleResetPasswordUser: {}", e.getMessage(), e);
            throw new OurException("Failed to reset password", 500);
        }
    }

    public UserDto handleForgotPasswordUser(String email, String newPassword) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found", 404));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return userMapper.toDto(user);
        } catch (OurException e) {
            logger.error("Error in handleForgotPasswordUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleForgotPasswordUser: {}", e.getMessage(), e);
            throw new OurException("Failed to update password", 500);
        }
    }

    public UserDto handleChangePasswordUser(String email, String currentPassword, String newPassword) {
        try {
            UserDto userDto = handleAuthenticateUser(email, currentPassword);
            System.out.println("Authenticated user for password change: " + userDto.getEmail());

            User user = userMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return userDto;
        } catch (OurException e) {
            logger.error("Error in handleChangePasswordUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleChangePasswordUser: {}", e.getMessage(), e);
            throw new OurException("Failed to change password", 500);
        }
    }

    public Response createUser(String dataJson) {
        logger.info("Creating new user");
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
            logger.info("User creation completed successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("User creation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("User creation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public List<UserDto> handleGetAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in handleGetAllUsers: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve users", 500);
        }
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
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("User not found", 404));
            return userMapper.toDto(user);
        } catch (OurException e) {
            logger.error("Error in handleGetUserById: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleGetUserById: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve user", 500);
        }
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
        try {
            logger.info("Updating user with ID: {}", userId);

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
            boolean privilegedChangeAllowed = currentUser == null || currentUser.hasRole("ADMIN");

            // Handle avatar upload
            if (avatar != null && !avatar.isEmpty()) {
                logger.debug("Updating avatar for user: {}", userId);
                // Delete old avatar if exists
                String oldAvatarPublicId = existingUser.getAvatarPublicId();
                if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                    cloudinaryService.deleteImage(oldAvatarPublicId);
                }

                // Upload new avatar
                var uploadResult = cloudinaryService.uploadImage(avatar);
                if (uploadResult.containsKey("error")) {
                    logger.error("Failed to upload avatar for user {}: {}", userId, uploadResult.get("error"));
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
                    logger.warn("Unauthorized attempt to change role for user {} by user: {}",
                            userId, currentUser != null ? currentUser.getEmail() : "unknown");
                    throw new OurException("Forbidden", 403);
                }
                existingUser.setRole(UserRole.valueOf(role));
            }

            if (status != null && !status.isEmpty()) {
                if (!privilegedChangeAllowed) {
                    logger.warn("Unauthorized attempt to change status for user {} by user: {}",
                            userId, currentUser != null ? currentUser.getEmail() : "unknown");
                    throw new OurException("Forbidden", 403);
                }
                existingUser.setStatus(UserStatus.valueOf(status));
            }

            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully: {}", userId);
            return userMapper.toDto(updatedUser);
        } catch (OurException e) {
            logger.error("Error in handleUpdateUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleUpdateUser: {}", e.getMessage(), e);
            throw new OurException("Failed to update user", 500);
        }
    }

    public Response updateUser(UUID userId, String dataJson, MultipartFile avatar) {
        logger.info("Updating user: {}", userId);
        Response response = new Response();

        try {
            UpdateUserRequest request = objectMapper.readValue(dataJson, UpdateUserRequest.class);
            String fullname = request.getFullname();
            String role = request.getRole();
            String status = request.getStatus();

            UserDto updatedUserDto = handleUpdateUser(userId, fullname, role, status, avatar);

            response.setMessage("User updated successfully");
            response.setUser(updatedUserDto);
            logger.info("User update completed successfully: {}", userId);
            return response;
        } catch (OurException e) {
            logger.error("User update failed with OurException for user {}: {}", userId, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("User update failed with unexpected error for user {}", userId, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public boolean handleDeleteUser(UUID userId) {
        try {
            logger.info("Deleting user with ID: {}", userId);
            UserDto user = handleGetUserById(userId);

            String avatarPublicId = user.getAvatarPublicId();
            if (avatarPublicId != null && !avatarPublicId.isEmpty()) {
                logger.debug("Deleting avatar for user: {}", userId);
                cloudinaryService.deleteImage(avatarPublicId);
            }

            userRepository.deleteById(userId);
            logger.info("User deleted successfully: {}", userId);
            return true;
        } catch (OurException e) {
            logger.error("Error in handleDeleteUser: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleDeleteUser: {}", e.getMessage(), e);
            throw new OurException("Failed to delete user", 500);
        }
    }

    public Response deleteUser(UUID userId) {
        logger.info("Deleting user: {}", userId);
        Response response = new Response();

        try {
            handleDeleteUser(userId);

            response.setMessage("User deleted successfully");
            logger.info("User deletion completed successfully: {}", userId);
            return response;
        } catch (OurException e) {
            logger.error("User deletion failed with OurException for user {}: {}", userId, e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("User deletion failed with unexpected error for user {}", userId, e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public UserDto handleFindByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .map(userMapper::toDto)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error in handleFindByEmail: {}", e.getMessage(), e);
            throw new OurException("Failed to find user by email", 500);
        }
    }

    public UserDto handleFindByUsername(String username) {
        try {
            return userRepository.findByUsername(username)
                    .map(userMapper::toDto)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error in handleFindByUsername: {}", e.getMessage(), e);
            throw new OurException("Failed to find user by username", 500);
        }
    }

    public UserDto handleFindById(UUID userId) {
        try {
            return userRepository.findById(userId)
                    .map(userMapper::toDto)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error in handleFindById: {}", e.getMessage(), e);
            throw new OurException("Failed to find user by ID", 500);
        }
    }

    public UserDto handleFindByIdentifier(String identifier) {
        try {
            boolean isEmailValid = handleIsValidEmail(identifier);
            UserDto user = null;
            if (isEmailValid) {
                user = handleFindByEmail(identifier);
            } else {
                user = handleFindByUsername(identifier);
            }

            return user;
        } catch (Exception e) {
            logger.error("Error in handleFindByIdentifier: {}", e.getMessage(), e);
            throw new OurException("Failed to find user by identifier", 500);
        }
    }

    public UserDto handleFindOAuth2User(String email, String provider, String providerId) {
        try {
            logger.debug("Finding OAuth2 user: email={}, provider={}, providerId={}", email, provider, providerId);

            // Tìm theo email và provider trước
            Optional<User> userByEmailAndProvider = userRepository.findByEmailAndOauthProvider(email, provider);
            if (userByEmailAndProvider.isPresent()) {
                return userMapper.toDto(userByEmailAndProvider.get());
            }

            // Nếu không tìm thấy, thử tìm theo email thôi (trường hợp user có thể đã tồn
            // tại nhưng chưa link provider)
            Optional<User> userByEmail = userRepository.findByEmail(email);
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                // Kiểm tra nếu user chưa có OAuth provider, có thể link
                if (user.getOauthProvider() == null || user.getOauthProvider().isEmpty()) {
                    logger.info("Found existing user by email, can be linked to OAuth provider");
                    return userMapper.toDto(user);
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("Error in handleFindOAuth2User: {}", e.getMessage(), e);
            throw new OurException("Failed to find OAuth2 user", 500);
        }
    }

    /**
     * Tìm user theo email và provider
     */
    public UserDto handleFindByEmailAndProvider(String email, String provider) {
        try {
            logger.debug("Finding user by email and provider: email={}, provider={}", email, provider);

            Optional<User> user = userRepository.findByEmailAndOauthProvider(email, provider);
            return user.map(userMapper::toDto).orElse(null);
        } catch (Exception e) {
            logger.error("Error in handleFindByEmailAndProvider: {}", e.getMessage(), e);
            throw new OurException("Failed to find user by email and provider", 500);
        }
    }

    /**
     * Tạo user mới từ OAuth2
     */
    public UserDto handleCreateOAuth2User(OAuth2UserDto oauth2UserDto) {
        try {
            logger.info("Creating OAuth2 user: email={}, provider={}", oauth2UserDto.getEmail(),
                    oauth2UserDto.getProvider());

            // Kiểm tra user đã tồn tại chưa
            if (userRepository.existsByEmail(oauth2UserDto.getEmail())) {
                // User đã tồn tại, cần link với OAuth provider
                Optional<User> existingUser = userRepository.findByEmail(oauth2UserDto.getEmail());
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    return handleLinkOAuth2Provider(user, oauth2UserDto);
                }
            }

            // Tạo username unique từ email hoặc name
            String username = handleGenerateUniqueUsername(oauth2UserDto);

            // Tạo user mới
            User newUser = new User(
                    username,
                    oauth2UserDto.getEmail(),
                    oauth2UserDto.getFirstName() != null ? oauth2UserDto.getFirstName()
                            : handleExtractFirstName(oauth2UserDto.getName()),
                    oauth2UserDto.getLastName() != null ? oauth2UserDto.getLastName()
                            : handleExtractLastName(oauth2UserDto.getName()),
                    oauth2UserDto.getProvider(),
                    oauth2UserDto.getProviderId(),
                    oauth2UserDto.getAvatarUrl());

            User savedUser = userRepository.save(newUser);
            logger.info("OAuth2 user created successfully with ID: {}", savedUser.getId());

            return userMapper.toDto(savedUser);
        } catch (OurException e) {
            logger.error("Error in handleCreateOAuth2User: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleCreateOAuth2User: {}", e.getMessage(), e);
            throw new OurException("Failed to create OAuth2 user", 500);
        }
    }

    /**
     * Cập nhật user OAuth2
     */
    public UserDto handleUpdateOAuth2User(UUID userId, OAuth2UserDto oauth2UserDto) {
        try {
            logger.info("Updating OAuth2 user: userId={}", userId);

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Cập nhật thông tin
            if (oauth2UserDto.getLastName() != null && oauth2UserDto.getFirstName() != null) {
                existingUser.setFullname(oauth2UserDto.getLastName() + " " + oauth2UserDto.getFirstName());
            }
            if (oauth2UserDto.getAvatarUrl() != null) {
                existingUser.setAvatarUrl(oauth2UserDto.getAvatarUrl());
            }

            if (existingUser.getOauthProvider() == null) {
                existingUser.setOauthProvider(oauth2UserDto.getProvider());
                existingUser.setOauthProviderId(oauth2UserDto.getProviderId());
                existingUser.setOAuthUser(true);
            }

            User updatedUser = userRepository.save(existingUser);
            logger.info("OAuth2 user updated successfully: userId={}", userId);

            return userMapper.toDto(updatedUser);
        } catch (OurException e) {
            logger.error("Error in handleUpdateOAuth2User: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleUpdateOAuth2User: {}", e.getMessage(), e);
            throw new OurException("Failed to update OAuth2 user", 500);
        }
    }

    /**
     * Link existing user với OAuth2 provider
     */
    private UserDto handleLinkOAuth2Provider(User existingUser, OAuth2UserDto oauth2UserDto) {
        try {
            logger.info("Linking existing user with OAuth2 provider: userId={}, provider={}",
                    existingUser.getId(), oauth2UserDto.getProvider());

            existingUser.setOauthProvider(oauth2UserDto.getProvider());
            existingUser.setOauthProviderId(oauth2UserDto.getProviderId());
            existingUser.setAvatarUrl(oauth2UserDto.getAvatarUrl());
            existingUser.setOAuthUser(true);

            // Cập nhật name nếu chưa có
            if (existingUser.getFullname() == null && oauth2UserDto.getFirstName() != null
                    && oauth2UserDto.getLastName() != null) {
                existingUser.setFullname(oauth2UserDto.getFirstName() + "" + oauth2UserDto.getLastName());
            }

            User savedUser = userRepository.save(existingUser);
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            logger.error("Error in handleLinkOAuth2Provider: {}", e.getMessage(), e);
            throw new OurException("Failed to link OAuth2 provider", 500);
        }
    }

    /**
     * Tạo username unique
     */
    private String handleGenerateUniqueUsername(OAuth2UserDto oauth2UserDto) {
        try {
            String baseUsername;

            if (oauth2UserDto.getUsername() != null && !oauth2UserDto.getUsername().isEmpty()) {
                baseUsername = oauth2UserDto.getUsername();
            } else {
                // Extract từ email
                String emailPrefix = oauth2UserDto.getEmail().split("@")[0];
                baseUsername = emailPrefix.replaceAll("[^a-zA-Z0-9]", "");
            }

            String username = baseUsername;
            int counter = 1;

            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter;
                counter++;
            }

            return username;
        } catch (Exception e) {
            logger.error("Error in handleGenerateUniqueUsername: {}", e.getMessage(), e);
            throw new OurException("Failed to generate unique username", 500);
        }
    }

    /**
     * Extract first name từ full name
     */
    private String handleExtractFirstName(String fullname) {
        try {
            if (fullname == null || fullname.trim().isEmpty())
                return null;

            String[] parts = fullname.trim().split("\\s+");
            return parts[0];
        } catch (Exception e) {
            logger.error("Error in handleExtractFirstName: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract last name từ full name
     */
    private String handleExtractLastName(String fullname) {
        try {
            if (fullname == null || fullname.trim().isEmpty())
                return null;

            String[] parts = fullname.trim().split("\\s+");
            if (parts.length > 1) {
                return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
            }
            return null;
        } catch (Exception e) {
            logger.error("Error in handleExtractLastName: {}", e.getMessage(), e);
            return null;
        }
    }

    // ========== Statistics Methods ==========

    /**
     * Get total number of users
     */
    public long handleGetTotalUsers() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            logger.error("Error in handleGetTotalUsers: {}", e.getMessage(), e);
            throw new OurException("Failed to get total users", 500);
        }
    }

    /**
     * Get users count by status
     */
    public long handleGetUsersByStatus(String status) {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> user.getStatus().toString().equalsIgnoreCase(status))
                    .count();
        } catch (Exception e) {
            logger.error("Error in handleGetUsersByStatus: {}", e.getMessage(), e);
            throw new OurException("Failed to get users by status", 500);
        }
    }

    /**
     * Get users created in date range
     */
    public long handleGetUsersCreatedInRange(String startDate, String endDate) {
        try {
            // Note: User entity doesn't have createdAt field in current implementation
            // This is a placeholder implementation
            // TODO: Add createdAt field to User entity for proper tracking
            logger.warn("handleGetUsersCreatedInRange called but User entity doesn't have createdAt field");
            return 0;
        } catch (Exception e) {
            logger.error("Error in handleGetUsersCreatedInRange: {}", e.getMessage(), e);
            throw new OurException("Failed to get users created in range", 500);
        }
    }

    /**
     * Get recent users
     */
    public List<UserDto> handleGetRecentUsers(int limit) {
        try {
            // Note: User entity doesn't have createdAt field in current implementation
            // This returns first N users instead
            return userRepository.findAll().stream()
                    .limit(limit)
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in handleGetRecentUsers: {}", e.getMessage(), e);
            throw new OurException("Failed to get recent users", 500);
        }
    }

    public Response getUserStats() {
        try {
            long totalUsers = handleGetTotalUsers();
            long activeUsers = handleGetUsersByStatus("active");
            long pendingUsers = handleGetUsersByStatus("pending");
            long bannedUsers = handleGetUsersByStatus("banned");

            Response response = new Response(200, "User statistics retrieved successfully");
            response.setAdditionalData(Map.of(
                    "totalUsers", totalUsers,
                    "activeUsers", activeUsers,
                    "pendingUsers", pendingUsers,
                    "bannedUsers", bannedUsers));
            return response;
        } catch (Exception e) {
            logger.error("Error in getUserStats: {}", e.getMessage(), e);
            return new Response(500, "Failed to get user statistics");
        }
    }

    public Response getUsersByStatus(String status) {
        try {
            long count = handleGetUsersByStatus(status);
            Response response = new Response(200, "Users by status retrieved successfully");
            response.setAdditionalData(Map.of("count", count));
            return response;
        } catch (Exception e) {
            logger.error("Error in getUsersByStatus: {}", e.getMessage(), e);
            return new Response(500, "Failed to get users by status");
        }
    }

    public Response getUsersCreatedInRange(String startDate, String endDate) {
        try {
            long count = handleGetUsersCreatedInRange(startDate, endDate);
            Response response = new Response(200, "Users created in range retrieved successfully");
            response.setAdditionalData(Map.of("count", count));
            return response;
        } catch (Exception e) {
            logger.error("Error in getUsersCreatedInRange: {}", e.getMessage(), e);
            return new Response(500, "Failed to get users created in range");
        }
    }

    public Response getRecentUsers(int limit) {
        try {
            List<UserDto> recentUsers = handleGetRecentUsers(limit);
            Response response = new Response(200, "Recent users retrieved successfully");
            response.setUsers(recentUsers);
            return response;
        } catch (Exception e) {
            logger.error("Error in getRecentUsers: {}", e.getMessage(), e);
            return new Response(500, "Failed to get recent users");
        }
    }

    public Response authenticateUser(String identifier, String password) {
        logger.info("Authenticating user");
        Response response = new Response();

        try {
            UserDto userDto = handleAuthenticateUser(identifier, password);

            response.setStatusCode(200);
            response.setMessage("User authenticated successfully");
            response.setUser(userDto);
            logger.info("User authentication completed successfully for identifier: {}", identifier);
            return response;
        } catch (OurException e) {
            logger.error("User authentication failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("User authentication failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response findUserByIdentifier(String identifier) {
        logger.info("Finding user by identifier: {}", identifier);
        Response response = new Response();

        try {
            UserDto userDto = handleFindByIdentifier(identifier);

            if (userDto == null) {
                return buildErrorResponse(404, "User not found");
            }

            response.setStatusCode(200);
            response.setMessage("User found successfully");
            response.setUser(userDto);
            logger.info("User found successfully for identifier: {}", identifier);
            return response;
        } catch (OurException e) {
            logger.error("Find user by identifier failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Find user by identifier failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response activateUser(String email) {
        logger.info("Activating user: {}", email);
        Response response = new Response();

        try {
            UserDto userDto = handleActivateUser(email);

            response.setStatusCode(200);
            response.setMessage("User activated successfully");
            response.setUser(userDto);
            logger.info("User activation completed successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("User activation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("User activation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response changePassword(String identifier, String dataJson) {
        logger.info("Changing password for identifier: {}", identifier);
        Response response = new Response();

        try {
            ChangePasswordRequest request = objectMapper.readValue(dataJson, ChangePasswordRequest.class);
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();

            UserDto userDto = handleChangePasswordUser(identifier, currentPassword, newPassword);

            response.setStatusCode(200);
            response.setMessage("Password changed successfully");
            response.setUser(userDto);
            logger.info("Password change completed successfully for identifier: {}", identifier);
            return response;
        } catch (OurException e) {
            logger.error("Password change failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Password change failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response resetPassword(String email) {
        logger.info("Resetting password for email: {}", email);
        Response response = new Response();

        try {
            String newPassword = handleResetPasswordUser(email);

            response.setStatusCode(200);
            response.setMessage("Password reset successfully");
            response.setAdditionalData(Map.of("newPassword", newPassword));
            logger.info("Password reset completed successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Password reset failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Password reset failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response forgotPassword(String email, String dataJson) {
        logger.info("Forgot password for email: {}", email);
        Response response = new Response();

        try {
            ForgotPasswordRequest request = objectMapper.readValue(dataJson, ForgotPasswordRequest.class);
            String newPassword = request.getPassword();

            UserDto userDto = handleForgotPasswordUser(email, newPassword);

            response.setStatusCode(200);
            response.setMessage("Password updated successfully");
            response.setUser(userDto);
            logger.info("Forgot password completed successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Forgot password failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Forgot password failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response findUserByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        Response response = new Response();

        try {
            UserDto userDto = handleFindByEmail(email);

            if (userDto == null) {
                return buildErrorResponse(404, "User not found");
            }

            response.setStatusCode(200);
            response.setMessage("User found successfully");
            response.setUser(userDto);
            logger.info("User found successfully for email: {}", email);
            return response;
        } catch (OurException e) {
            logger.error("Find user by email failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Find user by email failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }
}