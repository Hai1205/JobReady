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
import com.example.userservice.securities.*;
import com.example.userservice.services.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
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
        logger.info("Creating user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            logger.warn("Attempted to create user with existing email: {}", email);
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

        if (!role.isEmpty()) {
            user.setRole(UserRole.valueOf(role));
        }
        if (!status.isEmpty()) {
            user.setStatus(UserStatus.valueOf(status));
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    public UserDto handleActivateUser(String email) {
        logger.info("Activating user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.active);
        userRepository.save(user);
        logger.info("User activated successfully: {}", email);
        return userMapper.toDto(user);
    }

    public UserDto handleAuthenticateUser(String email, String currentPassword) {
        logger.debug("Authenticating user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found", 404));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Invalid password attempt for user: {}", email);
            throw new OurException("Invalid credentials", 400);
        }

        logger.debug("User authenticated successfully: {}", email);
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
        logger.info("Updating user with ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser().orElse(null);
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
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    public UserDto handleFindById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElse(null);
    }

    /**
     * Tìm user OAuth2 theo email, provider và providerId
     */
    public UserDto handleFindOAuth2User(String email, String provider, String providerId) {
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
    }

    /**
     * Tìm user theo email và provider
     */
    public UserDto handleFindByEmailAndProvider(String email, String provider) {
        logger.debug("Finding user by email and provider: email={}, provider={}", email, provider);

        Optional<User> user = userRepository.findByEmailAndOauthProvider(email, provider);
        return user.map(userMapper::toDto).orElse(null);
    }

    /**
     * Tạo user mới từ OAuth2
     */
    public UserDto handleCreateOAuth2User(OAuth2UserDto oauth2UserDto) {
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
    }

    /**
     * Cập nhật user OAuth2
     */
    public UserDto handleUpdateOAuth2User(UUID userId, OAuth2UserDto oauth2UserDto) {
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

        // Cập nhật provider info nếu chưa có
        if (existingUser.getOauthProvider() == null) {
            existingUser.setOauthProvider(oauth2UserDto.getProvider());
            existingUser.setOauthProviderId(oauth2UserDto.getProviderId());
            existingUser.setOAuthUser(true);
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("OAuth2 user updated successfully: userId={}", userId);

        return userMapper.toDto(updatedUser);
    }

    /**
     * Link existing user với OAuth2 provider
     */
    private UserDto handleLinkOAuth2Provider(User existingUser, OAuth2UserDto oauth2UserDto) {
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
    }

    /**
     * Tạo username unique
     */
    private String handleGenerateUniqueUsername(OAuth2UserDto oauth2UserDto) {
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
    }

    /**
     * Extract first name từ full name
     */
    private String handleExtractFirstName(String fullname) {
        if (fullname == null || fullname.trim().isEmpty())
            return null;

        String[] parts = fullname.trim().split("\\s+");
        return parts[0];
    }

    /**
     * Extract last name từ full name
     */
    private String handleExtractLastName(String fullname) {
        if (fullname == null || fullname.trim().isEmpty())
            return null;

        String[] parts = fullname.trim().split("\\s+");
        if (parts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
        return null;
    }
}