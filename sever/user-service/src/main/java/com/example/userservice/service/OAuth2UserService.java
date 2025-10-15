package com.example.userservice.service;

import com.example.userservice.dto.OAuth2UserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    /**
     * Kiểm tra nếu người dùng đã tồn tại trong hệ thống (RabbitMQ listener)
     */
    // @RabbitListener(queues = RabbitConfig.USER_OAUTH2_CHECK_QUEUE)
    // @SendTo(RabbitConfig.USER_OAUTH2_CHECK_REPLY_QUEUE)
    // public Map<String, Object> checkUserExistsRabbit(Map<String, String> request)
    // {
    // try {
    // String email = request.get("email");
    // String provider = request.get("provider");
    // String providerId = request.get("providerId");

    // logger.info("Received request to check user: email={}, provider={},
    // providerId={}",
    // email, provider, providerId);

    // // Tìm user theo email và provider
    // Optional<User> userOptional =
    // userRepository.findByEmailAndOauthProvider(email, provider);

    // Map<String, Object> response = new HashMap<>();

    // if (userOptional.isPresent()) {
    // User user = userOptional.get();
    // response.put("exists", true);
    // response.put("id", user.getId());
    // response.put("email", user.getEmail());
    // response.put("username", user.getUsername());
    // response.put("role", user.getRole().toString());
    // response.put("provider", user.getOauthProvider());
    // response.put("providerId", user.getOauthProviderId());
    // logger.info("User exists: {}", email);
    // } else {
    // response.put("exists", false);
    // logger.info("User does not exist: {}", email);
    // }

    // return response;
    // } catch (Exception e) {
    // logger.error("Error checking user exists", e);
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("exists", false);
    // errorResponse.put("error", e.getMessage());
    // return errorResponse;
    // }
    // }

    /**
     * Tạo người dùng mới từ thông tin OAuth2 (RabbitMQ listener)
     */
    // @RabbitListener(queues = RabbitConfig.USER_OAUTH2_CREATE_QUEUE)
    // @SendTo(RabbitConfig.USER_OAUTH2_CREATE_REPLY_QUEUE)
    // public Map<String, Object> createOAuth2UserRabbit(OAuth2UserDto
    // oauth2UserDto) {
    // try {
    // logger.info("Received request to create OAuth2 user: email={}, provider={}",
    // oauth2UserDto.getEmail(), oauth2UserDto.getProvider());

    // // Sử dụng phương thức tạo OAuth2 user hiện có
    // UserDto createdUser = createOAuth2User(oauth2UserDto);

    // // Trả về thông tin user
    // Map<String, Object> response = new HashMap<>();
    // response.put("id", createdUser.getId());
    // response.put("email", createdUser.getEmail());
    // response.put("username", createdUser.getUsername());
    // response.put("fullname", createdUser.getFullname());
    // response.put("role", "ROLE_USER");
    // response.put("avatarUrl", createdUser.getAvatarUrl());
    // response.put("provider", createdUser.getOauthProvider());
    // response.put("providerId", createdUser.getOauthProviderId());

    // return response;
    // } catch (Exception e) {
    // logger.error("Error creating OAuth2 user", e);
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("error", e.getMessage());
    // return errorResponse;
    // }
    // }

    /**
     * Cập nhật thông tin người dùng OAuth2 (RabbitMQ listener)
     */
    // @RabbitListener(queues = RabbitConfig.USER_OAUTH2_UPDATE_QUEUE)
    // @SendTo(RabbitConfig.USER_OAUTH2_UPDATE_REPLY_QUEUE)
    // public Map<String, Object> updateOAuth2UserRabbit(OAuth2UserDto
    // oauth2UserDto) {
    // try {
    // UUID userId = oauth2UserDto.getId();
    // logger.info("Received request to update OAuth2 user: id={}, email={}",
    // userId, oauth2UserDto.getEmail());

    // if (userId == null) {
    // logger.error("User ID is required for update");
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("error", "User ID is required for update");
    // return errorResponse;
    // }

    // // Sử dụng phương thức update OAuth2 user hiện có
    // UserDto updatedUser = updateOAuth2User(userId, oauth2UserDto);

    // // Trả về thông tin user
    // Map<String, Object> response = new HashMap<>();
    // response.put("id", updatedUser.getId());
    // response.put("email", updatedUser.getEmail());
    // response.put("username", updatedUser.getUsername());
    // response.put("fullname", updatedUser.getFullname());
    // response.put("role", "ROLE_USER");
    // response.put("avatarUrl", updatedUser.getAvatarUrl());
    // response.put("provider", updatedUser.getOauthProvider());
    // response.put("providerId", updatedUser.getOauthProviderId());

    // return response;
    // } catch (Exception e) {
    // logger.error("Error updating OAuth2 user", e);
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("error", e.getMessage());
    // return errorResponse;
    // }
    // }

    /**
     * Tìm user OAuth2 theo email, provider và providerId
     */
    public UserDto findOAuth2User(String email, String provider, String providerId) {
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
    public UserDto findByEmailAndProvider(String email, String provider) {
        logger.debug("Finding user by email and provider: email={}, provider={}", email, provider);

        Optional<User> user = userRepository.findByEmailAndOauthProvider(email, provider);
        return user.map(userMapper::toDto).orElse(null);
    }

    /**
     * Tạo user mới từ OAuth2
     */
    public UserDto createOAuth2User(OAuth2UserDto oauth2UserDto) {
        logger.info("Creating OAuth2 user: email={}, provider={}", oauth2UserDto.getEmail(),
                oauth2UserDto.getProvider());

        // Kiểm tra user đã tồn tại chưa
        if (userRepository.existsByEmail(oauth2UserDto.getEmail())) {
            // User đã tồn tại, cần link với OAuth provider
            Optional<User> existingUser = userRepository.findByEmail(oauth2UserDto.getEmail());
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                return linkOAuth2Provider(user, oauth2UserDto);
            }
        }

        // Tạo username unique từ email hoặc name
        String username = generateUniqueUsername(oauth2UserDto);

        // Tạo user mới
        User newUser = new User(
                username,
                oauth2UserDto.getEmail(),
                oauth2UserDto.getFirstName() != null ? oauth2UserDto.getFirstName()
                        : extractFirstName(oauth2UserDto.getName()),
                oauth2UserDto.getLastName() != null ? oauth2UserDto.getLastName()
                        : extractLastName(oauth2UserDto.getName()),
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
    public UserDto updateOAuth2User(UUID userId, OAuth2UserDto oauth2UserDto) {
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
    private UserDto linkOAuth2Provider(User existingUser, OAuth2UserDto oauth2UserDto) {
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
    private String generateUniqueUsername(OAuth2UserDto oauth2UserDto) {
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
    private String extractFirstName(String fullname) {
        if (fullname == null || fullname.trim().isEmpty())
            return null;

        String[] parts = fullname.trim().split("\\s+");
        return parts[0];
    }

    /**
     * Extract last name từ full name
     */
    private String extractLastName(String fullname) {
        if (fullname == null || fullname.trim().isEmpty())
            return null;

        String[] parts = fullname.trim().split("\\s+");
        if (parts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
        return null;
    }
}