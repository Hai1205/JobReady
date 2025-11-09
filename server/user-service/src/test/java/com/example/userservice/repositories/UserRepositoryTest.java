package com.example.userservice.repositories;

import com.example.userservice.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFullname("Test User");
        testUser.setRole(User.UserRole.user);
        testUser.setStatus(User.UserStatus.active);
    }

    @Test
    void testSaveUser() {
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void testFindByEmail() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        Optional<User> found = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testExistsByEmail() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testFindAll() {
        // Arrange
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password123");
        user1.setFullname("User One");
        user1.setRole(User.UserRole.user);
        user1.setStatus(User.UserStatus.active);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password456");
        user2.setFullname("User Two");
        user2.setRole(User.UserRole.user);
        user2.setStatus(User.UserStatus.active);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        User savedUser = entityManager.persist(testUser);
        UUID userId = savedUser.getId();
        entityManager.flush();

        // Act
        userRepository.deleteById(userId);
        entityManager.flush();

        // Assert
        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User savedUser = entityManager.persist(testUser);
        entityManager.flush();

        // Act
        savedUser.setFullname("Updated Name");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Assert
        assertEquals("Updated Name", updatedUser.getFullname());
    }
}
