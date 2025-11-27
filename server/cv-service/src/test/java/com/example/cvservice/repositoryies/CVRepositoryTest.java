package com.example.cvservice.repositoryies;

import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.PersonalInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for CVRepository
 * Tests database operations with H2 in-memory database
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.autoconfigure.exclude=org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.main.allow-bean-definition-overriding=true"
})
@Transactional
class CVRepositoryTest {

    @Autowired
    private CVRepository cvRepository;

    private UUID userId;
    private CV cv1;
    private CV cv2;

    @BeforeEach
    void setUp() {
        // Clear any existing data to ensure clean state
        cvRepository.deleteAll();

        userId = UUID.randomUUID();

        // Create separate PersonalInfo objects for each CV to avoid @OneToOne
        // constraint violations
        PersonalInfo personalInfo1 = new PersonalInfo();
        personalInfo1.setFullname("John Doe");
        personalInfo1.setEmail("john@example.com");

        PersonalInfo personalInfo2 = new PersonalInfo();
        personalInfo2.setFullname("Jane Smith");
        personalInfo2.setEmail("jane@example.com");

        cv1 = new CV(userId, "CV 1");
        cv1.setPersonalInfo(personalInfo1);
        cvRepository.save(cv1);

        cv2 = new CV(userId, "CV 2");
        cv2.setPersonalInfo(personalInfo2);
        cvRepository.save(cv2);
    }

    @Test
    void testFindById_Success() {
        // Act
        Optional<CV> result = cvRepository.findById(cv1.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(cv1.getId(), result.get().getId());
        assertEquals("CV 1", result.get().getTitle());
        assertEquals(userId, result.get().getUserId());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<CV> result = cvRepository.findById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        // Act
        List<CV> result = cvRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSave_NewCV() {
        // Arrange
        CV newCv = new CV(UUID.randomUUID(), "New CV");

        // Act
        CV saved = cvRepository.save(newCv);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New CV", saved.getTitle());
    }

    @Test
    void testSave_UpdateExistingCV() {
        // Arrange
        cv1.setTitle("Updated CV 1");

        // Act
        CV updated = cvRepository.save(cv1);

        // Assert
        assertEquals(cv1.getId(), updated.getId());
        assertEquals("Updated CV 1", updated.getTitle());
    }

    @Test
    void testFindByTitle_Success() {
        // Act
        Optional<CV> result = cvRepository.findByTitle("CV 1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CV 1", result.get().getTitle());
        assertEquals(cv1.getId(), result.get().getId());
    }

    @Test
    void testFindByTitle_NotFound() {
        // Act
        Optional<CV> result = cvRepository.findByTitle("Non-existent CV");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllByUserId() {
        // Act
        List<CV> result = cvRepository.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(cv -> cv.getUserId().equals(userId)));
    }

    @Test
    void testFindAllByUserId_NoCVs() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();

        // Act
        List<CV> result = cvRepository.findAllByUserId(differentUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByPersonalInfoEmail_Success() {
        // Act
        Optional<CV> result = cvRepository.findByPersonalInfoEmail("john@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getPersonalInfo().getEmail());
    }

    @Test
    void testFindByPersonalInfoEmail_NotFound() {
        // Act
        Optional<CV> result = cvRepository.findByPersonalInfoEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByPersonalInfoEmail_True() {
        // Act
        boolean result = cvRepository.existsByPersonalInfoEmail("john@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void testExistsByPersonalInfoEmail_False() {
        // Act
        boolean result = cvRepository.existsByPersonalInfoEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteById() {
        // Arrange
        UUID cvId = cv1.getId();

        // Act
        cvRepository.deleteById(cvId);

        // Assert
        Optional<CV> deleted = cvRepository.findById(cvId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCount() {
        // Act
        long count = cvRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testExistsById_True() {
        // Act
        boolean exists = cvRepository.existsById(cv1.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsById_False() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        boolean exists = cvRepository.existsById(nonExistentId);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindAll_EmptyDatabase() {
        // Arrange - Clear all data
        cvRepository.deleteAll();

        // Act
        List<CV> result = cvRepository.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSave_WithNullTitle() {
        // Arrange
        CV cvWithNullTitle = new CV(userId, null);

        // Act
        CV saved = cvRepository.save(cvWithNullTitle);

        // Assert
        assertNotNull(saved.getId());
        assertNull(saved.getTitle());
    }

    @Test
    void testFindByTitle_CaseSensitive() {
        // Arrange
        CV cvCase = new CV(userId, "Test CV");
        cvRepository.save(cvCase);

        // Act
        Optional<CV> result = cvRepository.findByTitle("test cv");

        // Assert
        assertFalse(result.isPresent()); // Should be case sensitive
    }
}