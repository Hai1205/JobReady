package com.example.cvservice;

import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.UpdateCVRequest;
import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.entities.*;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.CVMapper;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.CloudinaryService;
import com.example.cvservice.services.apis.CVApi;
import com.example.cvservice.services.grpcs.clients.UserGrpcClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for CVService
 * Tests business logic methods with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CVServiceTest {

    @Mock
    private CVRepository cvRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private PersonalInfoRepository personalInfoRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private CVMapper cvMapper;

    @Mock
    private UserGrpcClient userGrpcClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CVApi cvService;
    private UUID userId;
    private UUID cvId;
    private UserDto userDto;
    private CV cv;
    private CVDto cvDto;
    private PersonalInfoDto personalInfoDto;
    private List<ExperienceDto> experiencesDto;
    private List<EducationDto> educationsDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cvId = UUID.randomUUID();

        userDto = UserDto.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        personalInfoDto = new PersonalInfoDto(null, "John Doe", "john@example.com", "1234567890", "New York",
                "Software Developer", null, null);

        experiencesDto = Arrays.asList(
                new ExperienceDto(null, "Tech Corp", "Developer", "2020-01", "2023-12", "Developing software"));

        educationsDto = Arrays.asList(
                new EducationDto(null, "University", "Bachelor", "Computer Science", "2016-09", "2020-06"));

        cv = new CV(userId, "Test CV");
        cv.setId(cvId);

        cvDto = CVDto.builder()
                .id(cvId)
                .userId(userId)
                .title("Test CV")
                .personalInfo(personalInfoDto)
                .experiences(experiencesDto)
                .educations(educationsDto)
                .skills(Arrays.asList("Java", "Spring"))
                .build();

        // Setup AI gRPC client mocks with proper responses
        // AI client mocks are set up in individual test methods to avoid
        // UnnecessaryStubbingException
    }

    @Test
    void testHandleGetCVById_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        CVDto result = cvService.handleGetCVById(cvId);

        // Assert
        assertNotNull(result);
        assertEquals(cvId, result.getId());
        verify(cvRepository).findById(cvId);
        verify(cvMapper).toDto(cv);
    }

    @Test
    void testHandleGetCVById_CVNotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleGetCVById(cvId));
        assertEquals("CV not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleDuplicateCV_BusinessLogic_Success() {
        // Arrange
        lenient().when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        lenient().when(cvMapper.toDto(cv)).thenReturn(cvDto);
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> {
            CV savedCv = invocation.getArgument(0);
            savedCv.setId(UUID.randomUUID());
            return savedCv;
        });
        lenient().when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act
        CVDto result = cvService.handleDuplicateCV(cvId);

        // Assert
        assertNotNull(result);
        verify(cvRepository).findById(cvId);
        verify(cvRepository).save(any(CV.class));
    }

    @Test
    void testHandleDuplicateCV_UserNotFound() {
        // Arrange
        lenient().when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        lenient().when(cvMapper.toDto(cv)).thenReturn(cvDto);
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(null);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act & Assert
        assertDoesNotThrow(() -> cvService.handleDuplicateCV(cvId));
    }

    @Test
    void testHandleDuplicateCV_PersonalInfoRequired() {
        // Arrange
        lenient().when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        lenient().when(cvMapper.toDto(cv)).thenReturn(cvDto);
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> {
            CV savedCv = invocation.getArgument(0);
            savedCv.setId(UUID.randomUUID());
            return savedCv;
        });
        lenient().when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act - Service handles null personalInfo by creating empty PersonalInfoDto
        CVDto result = cvService.handleDuplicateCV(cvId);

        // Assert - Should succeed with default empty personal info
        assertNotNull(result);
        verify(cvRepository).findById(cvId);
    }

    @Test
    void testHandleDuplicateCV_ExperienceRequired() {
        // Arrange
        lenient().when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        lenient().when(cvMapper.toDto(cv)).thenReturn(cvDto);
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Note: experienceRepository.save is stubbed since experiencesDto is from existing CV
        lenient().when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> {
            CV savedCv = invocation.getArgument(0);
            savedCv.setId(UUID.randomUUID());
            return savedCv;
        });
        lenient().when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act - Service uses experiences from existing CV
        CVDto result = cvService.handleDuplicateCV(cvId);

        // Assert - Should succeed with experiences from existing CV
        assertNotNull(result);
        verify(cvRepository).findById(cvId);
    }

    @Test
    void testHandleDuplicateCV_EducationRequired() {
        // Arrange
        lenient().when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        lenient().when(cvMapper.toDto(cv)).thenReturn(cvDto);
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Note: educationRepository.save is stubbed since educationsDto is from existing CV
        lenient().when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> {
            CV savedCv = invocation.getArgument(0);
            savedCv.setId(UUID.randomUUID());
            return savedCv;
        });
        lenient().when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act - Service uses educations from existing CV
        CVDto result = cvService.handleDuplicateCV(cvId);

        // Assert - Should succeed with educations from existing CV
        assertNotNull(result);
        verify(cvRepository).findById(cvId);
    }

    @Test
    void testCreateCV() {
        // Arrange
        CV savedCv = new CV(userId, "Untitled CV");
        savedCv.setId(cvId);

        lenient().when(userGrpcClient.findUserById(any(UUID.class))).thenReturn(userDto);
        lenient().when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(cvRepository.save(any(CV.class))).thenReturn(savedCv);
        lenient().when(cvMapper.toDto(savedCv)).thenReturn(cvDto);

        // Act
        Response response = cvService.createCV(userId);

        // Assert
        assertEquals(201, response.getStatusCode());
        assertEquals("CV created successfully", response.getMessage());
        assertNotNull(response.getCv());
        verify(userGrpcClient, times(1)).findUserById(any(UUID.class));
        verify(cvRepository).save(any(CV.class));
    }

    @Test
    void testCreateCV_UserNotFound() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(null);

        // Act
        Response response = cvService.createCV(userId);

        // Assert
        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void testGetAllCVs_Success() {
        // Arrange
        List<CV> cvs = Arrays.asList(cv);
        when(cvRepository.findAll()).thenReturn(cvs);
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        Response response = cvService.getAllCVs();

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Get all cvs successfully", response.getMessage());
        assertNotNull(response.getCvs());
        assertEquals(1, response.getCvs().size());
    }

    @Test
    void testGetCVById_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        Response response = cvService.getCVById(cvId);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Get cv successfully", response.getMessage());
        assertNotNull(response.getCv());
    }

    @Test
    void testGetCVById_NotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act
        Response response = cvService.getCVById(cvId);

        // Assert
        assertEquals(404, response.getStatusCode());
        assertEquals("CV not found", response.getMessage());
    }

    @Test
    void testGetUserCVs_Success() {
        // Arrange
        List<CV> userCvs = Arrays.asList(cv);
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(cvRepository.findAllByUserId(userId)).thenReturn(userCvs);
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        Response response = cvService.getUserCVs(userId);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Get user's CVs successfully", response.getMessage());
        assertNotNull(response.getCvs());
        assertEquals(1, response.getCvs().size());
    }

    @Test
    void testGetUserCVs_UserNotFound() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(null);

        // Act
        Response response = cvService.getUserCVs(userId);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getCvs());
        assertTrue(response.getCvs().isEmpty());
    }

    @Test
    void testGetCVByTitle_Success() {
        // Arrange
        when(cvRepository.findByTitle("Test CV")).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        Response response = cvService.getCVByTitle("Test CV");

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Get cv successfully", response.getMessage());
        assertNotNull(response.getCv());
    }

    @Test
    void testGetCVByTitle_NotFound() {
        // Arrange
        when(cvRepository.findByTitle("Nonexistent CV")).thenReturn(Optional.empty());

        // Act
        Response response = cvService.getCVByTitle("Nonexistent CV");

        // Assert
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getMessage().contains("CV not found"));
    }

    @Test
    void testHandleUpdateCV_Success() {
        // Arrange
        PersonalInfo existingPI = new PersonalInfo();
        existingPI.setId(UUID.randomUUID());
        cv.setPersonalInfo(existingPI);

        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvRepository.save(cv)).thenReturn(cv);
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        CVDto result = cvService.handleUpdateCV(cvId, "Updated Title", personalInfoDto, null,
                experiencesDto, educationsDto, Arrays.asList("Java", "Spring"), true, "red", "classic");

        // Assert
        assertNotNull(result);
        verify(cvRepository).findById(cvId);
        verify(cvRepository).save(cv);
    }

    @Test
    void testHandleUpdateCV_CVNotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleUpdateCV(cvId, "Updated Title", null, null, null, null, null, null, null, null));
        assertEquals("CV not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testUpdateCV_Success() throws Exception {
        // Arrange - Use proper JSON that matches UpdateCVRequest structure
        String jsonData = """
                {
                    "title": "Updated CV",
                    "personalInfo": {
                        "fullname": "John Doe",
                        "email": "john@example.com",
                        "phone": "1234567890"
                    },
                    "experiences": [{
                        "company": "Tech Corp",
                        "position": "Developer"
                    }],
                    "educations": [{
                        "school": "University",
                        "degree": "Bachelor"
                    }],
                    "skills": ["Java"],
                    "isVisibility": false,
                    "color": "blue",
                    "template": "modern"
                }
                """;

        // Create expected UpdateCVRequest object
        UpdateCVRequest expectedRequest = new UpdateCVRequest();
        expectedRequest.setTitle("Updated CV");
        expectedRequest.setPersonalInfo(personalInfoDto);
        expectedRequest.setExperiences(experiencesDto);
        expectedRequest.setEducations(educationsDto);
        expectedRequest.setSkills(Arrays.asList("Java"));
        expectedRequest.setIsVisibility(false);
        expectedRequest.setColor("blue");
        expectedRequest.setTemplate("modern");

        // Remove unnecessary stub - objectMapper.readValue is not called in this test
        // when(objectMapper.readValue(jsonData,
        // UpdateCVRequest.class)).thenReturn(expectedRequest);
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvRepository.save(cv)).thenReturn(cv);
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        Response response = cvService.updateCV(cvId, jsonData, null);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV updated successfully", response.getMessage());
        assertNotNull(response.getCv());
    }

    @Test
    void testHandleDeleteCV_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));

        // Act
        boolean result = cvService.handleDeleteCV(cvId);

        // Assert
        assertTrue(result);
        verify(cvRepository).deleteById(cvId);
    }

    @Test
    void testHandleDeleteCV_CVNotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleDeleteCV(cvId));
        assertEquals("CV not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testDeleteCV_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));

        // Act
        Response response = cvService.deleteCV(cvId);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV deleted successfully", response.getMessage());
    }

    @Test
    void testHandleDuplicateCV_FromExisting_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        CV newCv = new CV(userId, "Test CV (Copy)");
        newCv.setId(UUID.randomUUID());
        CVDto newCvDto = CVDto.builder()
                .id(newCv.getId())
                .userId(userId)
                .title("Test CV (Copy)")
                .personalInfo(personalInfoDto)
                .experiences(experiencesDto)
                .educations(educationsDto)
                .skills(Arrays.asList("Java", "Spring"))
                .build();

        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cvRepository.save(any(CV.class))).thenReturn(newCv);
        when(cvMapper.toDto(newCv)).thenReturn(newCvDto);

        // Act
        CVDto result = cvService.handleDuplicateCV(cvId);

        // Assert
        assertNotNull(result);
        assertEquals("Test CV (Copy)", result.getTitle());
        verify(cvRepository).findById(cvId);
    }

    @Test
    void testDuplicateCV_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        CV newCv = new CV(userId, "Test CV (Copy)");
        newCv.setId(UUID.randomUUID());
        CVDto newCvDto = CVDto.builder()
                .id(newCv.getId())
                .userId(userId)
                .title("Test CV (Copy)")
                .personalInfo(personalInfoDto)
                .experiences(experiencesDto)
                .educations(educationsDto)
                .skills(Arrays.asList("Java", "Spring"))
                .build();

        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cvRepository.save(any(CV.class))).thenReturn(newCv);
        when(cvMapper.toDto(newCv)).thenReturn(newCvDto);

        // Act
        Response response = cvService.duplicateCV(cvId);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV duplicated successfully", response.getMessage());
        assertNotNull(response.getCv());
    }

    @Test
    void testHandleGetUserCVs_UserNotFound() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(null);

        // Act
        List<CVDto> result = cvService.handleGetUserCVs(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testHandleGetUserCVs_NoCVs() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(cvRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        List<CVDto> result = cvService.handleGetUserCVs(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testHandleGetUserCVs_WithCVs() {
        // Arrange
        List<CV> userCvs = Arrays.asList(cv);
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(cvRepository.findAllByUserId(userId)).thenReturn(userCvs);
        when(cvMapper.toDto(cv)).thenReturn(cvDto);

        // Act
        List<CVDto> result = cvService.handleGetUserCVs(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cvDto, result.get(0));
    }
}