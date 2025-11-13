package com.example.userservice.services;

import com.example.userservice.dtos.OAuth2UserDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.entities.User;
import com.example.userservice.exceptions.OurException;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.apis.UserApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile avatarFile;

    @InjectMocks
    private UserApi userService;

    private User mockUser;
    private UserDto mockUserDto;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        // Set private fields for testing
        ReflectionTestUtils.setField(userService, "privateChars",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*");
        ReflectionTestUtils.setField(userService, "passwordLength", 12);

        // Mock user entity
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setFullname("Test User");
        mockUser.setPassword("encoded_password");
        mockUser.setRole(User.UserRole.user);
        mockUser.setStatus(User.UserStatus.active);

        // Mock user DTO
        mockUserDto = new UserDto();
        mockUserDto.setId(testUserId);
        mockUserDto.setEmail("test@example.com");
        mockUserDto.setUsername("testuser");
        mockUserDto.setFullname("Test User");
        mockUserDto.setRole("user");
        mockUserDto.setStatus("active");
    }

    // ==================== CREATE USER TESTS ====================

    @Test
    void testHandleCreateUser_Success() {
        // Arrange
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";
        String fullname = "New User";
        String role = "user";
        String status = "active";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateUser(username, email, password, fullname, role, status, null);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleCreateUser_WithNullEmail_ThrowsException() {
        // Arrange
        String username = "newuser";
        String email = null;
        String password = "password123";
        String fullname = "New User";

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleCreateUser(username, email, password, fullname, "", "", null);
        });

        assertEquals("Email is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testHandleCreateUser_WithEmptyEmail_ThrowsException() {
        // Arrange
        String email = "";

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleCreateUser("user", email, "pass", "Name", "", "", null);
        });

        assertEquals("Email is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void testHandleCreateUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleCreateUser("user", email, "pass", "Name", "", "", null);
        });

        assertEquals("Email already exists", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testHandleCreateUser_WithNullUsername_GeneratesFromEmail() {
        // Arrange
        String email = "newuser@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateUser(null, email, "pass", "Name", "", "", null);

        // Assert
        assertNotNull(result);
        verify(userRepository)
                .save(argThat(user -> user.getUsername() != null && user.getUsername().equals("newuser")));
    }

    @Test
    void testHandleCreateUser_WithEmptyUsername_GeneratesFromEmail() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateUser("", email, "pass", "Name", "", "", null);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getUsername() != null));
    }

    @Test
    void testHandleCreateUser_WithNullPassword_GeneratesRandomPassword() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateUser("user", email, null, "Name", "", "", null);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleCreateUser_WithEmptyPassword_GeneratesRandomPassword() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateUser("user", email, "", "Name", "", "", null);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void testHandleCreateUser_WithAvatar_UploadsSuccessfully() {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://cloudinary.com/avatar.jpg");
        uploadResult.put("publicId", "avatar_123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(cloudinaryService.uploadImage(avatarFile)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);
        when(avatarFile.isEmpty()).thenReturn(false);

        // Act
        UserDto result = userService.handleCreateUser("user", "test@example.com", "pass", "Name", "", "", avatarFile);

        // Assert
        assertNotNull(result);
        verify(cloudinaryService).uploadImage(avatarFile);
        verify(userRepository).save(argThat(user -> user.getAvatarUrl() != null && user.getAvatarPublicId() != null));
    }

    @Test
    void testHandleCreateUser_AvatarUploadFails_ThrowsException() {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("error", "Upload failed");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(cloudinaryService.uploadImage(avatarFile)).thenReturn(uploadResult);
        when(avatarFile.isEmpty()).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.handleCreateUser("user", "test@example.com", "pass", "Name", "", "", avatarFile);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    void testHandleAuthenticateUser_WithEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleAuthenticateUser(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, mockUser.getPassword());
    }

    @Test
    void testHandleAuthenticateUser_WithUsername_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleAuthenticateUser(username, password);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, mockUser.getPassword());
    }

    @Test
    void testHandleAuthenticateUser_UserNotFound_ThrowsException() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleAuthenticateUser(email, "password");
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleAuthenticateUser_InvalidPassword_ThrowsException() {
        // Arrange
        String email = "test@example.com";
        String wrongPassword = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(wrongPassword, mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleAuthenticateUser(email, wrongPassword);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    // ==================== ACTIVATE USER TESTS ====================

    @Test
    void testHandleActivateUser_Success() {
        // Arrange
        mockUser.setStatus(User.UserStatus.pending);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleActivateUser(mockUser.getEmail());

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getStatus() == User.UserStatus.active));
    }

    @Test
    void testHandleActivateUser_UserNotFound_ThrowsException() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.handleActivateUser(email);
        });
    }

    // ==================== PASSWORD MANAGEMENT TESTS ====================

    @Test
    void testHandleGenerateRandomPassword_GeneratesValidPassword() {
        // Act
        String password = userService.handleGenerateRandomPassword();

        // Assert
        assertNotNull(password);
        assertEquals(12, password.length());
    }

    @Test
    void testHandleResetPasswordUser_Success() {
        // Arrange
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        String newPassword = userService.handleResetPasswordUser(mockUser.getEmail());

        // Assert
        assertNotNull(newPassword);
        assertEquals(12, newPassword.length());
        verify(userRepository).save(argThat(user -> user.getPassword().equals("new_encoded_password")));
    }

    @Test
    void testHandleResetPasswordUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleResetPasswordUser("notfound@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleForgotPasswordUser_Success() {
        // Arrange
        String newPassword = "newPassword123";
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("new_encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleForgotPasswordUser(mockUser.getEmail(), newPassword);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleChangePasswordUser_Success() {
        // Arrange
        String currentPassword = "currentPass";
        String newPassword = "newPass";

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(currentPassword, mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("new_encoded");
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);
        when(userMapper.toEntity(mockUserDto)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserDto result = userService.handleChangePasswordUser(mockUser.getEmail(), currentPassword, newPassword);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(newPassword);
        verify(userMapper).toEntity(mockUserDto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testHandleChangePasswordUser_InvalidCurrentPassword_ThrowsException() {
        // Arrange
        String currentPassword = "wrongPass";
        String newPassword = "newPass";

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(currentPassword, mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleChangePasswordUser(mockUser.getEmail(), currentPassword, newPassword);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== GET USER TESTS ====================

    @Test
    void testHandleGetAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(mockUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        List<UserDto> result = userService.handleGetAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testHandleGetUserById_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleGetUserById(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        verify(userRepository).findById(testUserId);
    }

    @Test
    void testHandleGetUserById_NotFound_ThrowsException() {
        // Arrange
        UUID notFoundId = UUID.randomUUID();
        when(userRepository.findById(notFoundId)).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            userService.handleGetUserById(notFoundId);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleFindByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindByEmail(mockUser.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
    }

    @Test
    void testHandleFindByEmail_NotFound_ReturnsNull() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        UserDto result = userService.handleFindByEmail("notfound@example.com");

        // Assert
        assertNull(result);
    }

    @Test
    void testHandleFindByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindByUsername(mockUser.getUsername());

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    void testHandleFindById_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindById(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
    }

    @Test
    void testHandleFindByIdentifier_WithEmail_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindByIdentifier(email);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testHandleFindByIdentifier_WithUsername_Success() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindByIdentifier(username);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).findByEmail(anyString());
    }

    // ==================== UPDATE USER TESTS ====================

    @Test
    void testHandleUpdateUser_UpdateFullname_Success() {
        // Arrange
        String newFullname = "Updated Name";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleUpdateUser(testUserId, newFullname, "", "", null);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getFullname().equals(newFullname)));
    }

    @Test
    void testHandleUpdateUser_UpdateAvatar_Success() {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://new-avatar.jpg");
        uploadResult.put("publicId", "new_public_id");

        mockUser.setAvatarPublicId("old_public_id");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(avatarFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImage(avatarFile)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleUpdateUser(testUserId, "", "", "", avatarFile);

        // Assert
        assertNotNull(result);
        verify(cloudinaryService).deleteImage("old_public_id");
        verify(cloudinaryService).uploadImage(avatarFile);
    }

    @Test
    void testHandleUpdateUser_UserNotFound_ThrowsException() {
        // Arrange
        UUID notFoundId = UUID.randomUUID();
        when(userRepository.findById(notFoundId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.handleUpdateUser(notFoundId, "Name", "", "", null);
        });
    }

    // ==================== DELETE USER TESTS ====================

    @Test
    void testHandleDeleteUser_Success() {
        // Arrange
        mockUserDto.setAvatarPublicId("avatar_123");

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("status", "deleted");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);
        when(cloudinaryService.deleteImage(anyString())).thenReturn(deleteResult);
        doNothing().when(userRepository).deleteById(testUserId);

        // Act
        boolean result = userService.handleDeleteUser(testUserId);

        // Assert
        assertTrue(result);
        verify(cloudinaryService).deleteImage("avatar_123");
        verify(userRepository).deleteById(testUserId);
    }

    @Test
    void testHandleDeleteUser_WithoutAvatar_Success() {
        // Arrange
        mockUserDto.setAvatarPublicId(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);
        doNothing().when(userRepository).deleteById(testUserId);

        // Act
        boolean result = userService.handleDeleteUser(testUserId);

        // Assert
        assertTrue(result);
        verify(cloudinaryService, never()).deleteImage(anyString());
        verify(userRepository).deleteById(testUserId);
    }

    // ==================== OAUTH2 TESTS ====================

    @Test
    void testHandleCreateOAuth2User_NewUser_Success() {
        // Arrange
        OAuth2UserDto oauth2UserDto = new OAuth2UserDto();
        oauth2UserDto.setEmail("oauth@example.com");
        oauth2UserDto.setProvider("google");
        oauth2UserDto.setProviderId("google_123");
        oauth2UserDto.setFirstName("John");
        oauth2UserDto.setLastName("Doe");
        oauth2UserDto.setAvatarUrl("https://avatar.com/image.jpg");

        when(userRepository.existsByEmail(oauth2UserDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateOAuth2User(oauth2UserDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getOauthProvider().equals("google") &&
                user.getOauthProviderId().equals("google_123")));
    }

    @Test
    void testHandleCreateOAuth2User_ExistingUser_LinksProvider() {
        // Arrange
        OAuth2UserDto oauth2UserDto = new OAuth2UserDto();
        oauth2UserDto.setEmail("existing@example.com");
        oauth2UserDto.setProvider("google");
        oauth2UserDto.setProviderId("google_123");

        mockUser.setOauthProvider(null); // User exists but not linked to OAuth

        when(userRepository.existsByEmail(oauth2UserDto.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(oauth2UserDto.getEmail())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleCreateOAuth2User(oauth2UserDto);

        // Assert
        assertNotNull(result);
        verify(userRepository)
                .save(argThat(user -> user.getOauthProvider() != null && user.getOauthProvider().equals("google")));
    }

    @Test
    void testHandleFindOAuth2User_ByEmailAndProvider_Success() {
        // Arrange
        String email = "oauth@example.com";
        String provider = "google";
        String providerId = "google_123";

        when(userRepository.findByEmailAndOauthProvider(email, provider)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindOAuth2User(email, provider, providerId);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmailAndOauthProvider(email, provider);
    }

    @Test
    void testHandleFindOAuth2User_ByEmailOnly_Success() {
        // Arrange
        String email = "oauth@example.com";
        String provider = "google";
        String providerId = "google_123";

        mockUser.setOauthProvider(null); // User exists but not linked

        when(userRepository.findByEmailAndOauthProvider(email, provider)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleFindOAuth2User(email, provider, providerId);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testHandleFindOAuth2User_NotFound_ReturnsNull() {
        // Arrange
        when(userRepository.findByEmailAndOauthProvider(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        UserDto result = userService.handleFindOAuth2User("notfound@example.com", "google", "123");

        // Assert
        assertNull(result);
    }

    @Test
    void testHandleUpdateOAuth2User_Success() {
        // Arrange
        OAuth2UserDto oauth2UserDto = new OAuth2UserDto();
        oauth2UserDto.setFirstName("John");
        oauth2UserDto.setLastName("Doe");
        oauth2UserDto.setAvatarUrl("https://new-avatar.jpg");
        oauth2UserDto.setProvider("google");
        oauth2UserDto.setProviderId("google_123");

        mockUser.setOauthProvider(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(User.class))).thenReturn(mockUserDto);

        // Act
        UserDto result = userService.handleUpdateOAuth2User(testUserId, oauth2UserDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getOauthProvider() != null &&
                user.getAvatarUrl().equals("https://new-avatar.jpg")));
    }
}
