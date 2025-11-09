package com.example.cvservice;

import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.AnalyzeCVWithJDRequest;
import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.entities.*;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.CVMapper;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.CloudinaryService;
import com.example.cvservice.services.JobDescriptionParserService;
import com.example.cvservice.services.apis.CVService;
import com.example.cvservice.services.grpcs.AIGrpcClient;
import com.example.cvservice.services.grpcs.UserGrpcClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private JobDescriptionParserService jobDescriptionParserService;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private CVMapper cvMapper;

    @Mock
    private UserGrpcClient userGrpcClient;

    @Mock
    private AIGrpcClient aiGrpcClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CVService cvService;
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
        MultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = Map.of("url", "http://cloudinary.com/avatar.jpg", "publicId", "public_id");

        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(cloudinaryService.uploadImage(avatar)).thenReturn(uploadResult);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cvRepository.save(any(CV.class))).thenAnswer(invocation -> {
            CV savedCv = invocation.getArgument(0);
            savedCv.setId(UUID.randomUUID());
            return savedCv;
        });
        when(cvMapper.toDto(any(CV.class))).thenReturn(cvDto);

        // Act
        CVDto result = cvService.handleDuplicateCV(userId, "Test CV", personalInfoDto, avatar,
                experiencesDto, educationsDto, Arrays.asList("Java"), "PRIVATE", "blue", "modern");

        // Assert
        assertNotNull(result);
        verify(userGrpcClient).findUserById(userId);
        verify(cloudinaryService).uploadImage(avatar);
        verify(personalInfoRepository).save(any(PersonalInfo.class));
        verify(experienceRepository).save(any(Experience.class));
        verify(educationRepository).save(any(Education.class));
        verify(cvRepository).save(any(CV.class));
    }

    @Test
    void testHandleDuplicateCV_UserNotFound() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(null);

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleDuplicateCV(userId, "Test CV", personalInfoDto, null,
                        experiencesDto, educationsDto, Arrays.asList("Java"), "PRIVATE", "blue", "modern"));
        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleDuplicateCV_PersonalInfoRequired() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleDuplicateCV(userId, "Test CV", null, null,
                        experiencesDto, educationsDto, Arrays.asList("Java"), "PRIVATE", "blue", "modern"));
        assertEquals("Personal info is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void testHandleDuplicateCV_ExperienceRequired() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleDuplicateCV(userId, "Test CV", personalInfoDto, null,
                        null, educationsDto, Arrays.asList("Java"), "PRIVATE", "blue", "modern"));
        assertEquals("At least one experience is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void testHandleDuplicateCV_EducationRequired() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);

        // Act & Assert
        OurException exception = assertThrows(OurException.class,
                () -> cvService.handleDuplicateCV(userId, "Test CV", personalInfoDto, null,
                        experiencesDto, null, Arrays.asList("Java"), "PRIVATE", "blue", "modern"));
        assertEquals("At least one education is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void testCreateCV_Success() {
        // Arrange
        CV savedCv = new CV(userId, "Untitled CV");
        savedCv.setId(cvId);

        when(userGrpcClient.findUserById(userId)).thenReturn(userDto);
        when(cvRepository.save(any(CV.class))).thenReturn(savedCv);
        when(cvMapper.toDto(savedCv)).thenReturn(cvDto);

        // Act
        Response response = cvService.createCV(userId);

        // Assert
        assertEquals(201, response.getStatusCode());
        assertEquals("CV created successfully", response.getMessage());
        assertNotNull(response.getCv());
        verify(userGrpcClient).findUserById(userId);
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
    void testAnalyzeCV_Success() throws Exception {
        // Setup AI gRPC client mock for this test
        JobDescriptionResult jdResult = new JobDescriptionResult();
        jdResult.setJobTitle("Software Developer");
        jdResult.setCompany("Tech Corp");
        jdResult.setResponsibilities(Arrays.asList("Develop software", "Write tests"));
        jdResult.setRequirements(Arrays.asList("Java experience", "Spring Boot"));
        jdResult.setRequiredSkills(Arrays.asList("Java", "Spring", "SQL"));

        AIResponseDto analyzeResponse = AIResponseDto.builder()
                .analyzeResult("Good CV with strong technical skills")
                .suggestions(
                        Arrays.asList(AISuggestionDto.builder().suggestion("Add more details to projects").build()))
                .matchScore(85.5)
                .missingKeywords(Arrays.asList("Docker", "Kubernetes"))
                .jdResult(jdResult)
                .build();

        lenient().doReturn(analyzeResponse).when(aiGrpcClient).analyzeCV(any(CVDto.class));

        // Arrange - Use proper JSON that matches CreateCVRequest structure
        String jsonData = """
                {
                    "title": "Test CV",
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
                    "privacy": "PRIVATE",
                    "color": "blue",
                    "template": "modern"
                }
                """;

        // Act
        Response response = cvService.analyzeCV(jsonData);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV analyzed successfully", response.getMessage());
        assertEquals("Good CV with strong technical skills", response.getAnalyze());
        assertNotNull(response.getSuggestions());
    }

    @Test
    void testAnalyzeCV_InvalidJson() {
        // Act
        Response response = cvService.analyzeCV("invalid json");

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().startsWith("Error") || response.getMessage().contains("JSON"));
    }

    @Test
    void testImproveCV_Success() throws Exception {
        // Setup AI gRPC client mock for this test
        AIResponseDto improveResponse = AIResponseDto.builder()
                .improved("Enhanced summary with more impact and quantifiable achievements")
                .build();

        lenient().doReturn(improveResponse).when(aiGrpcClient).improveCV(anyString(), anyString());

        // Arrange - Use proper JSON that matches ImproveCVRequest structure
        String jsonData = """
                {
                    "section": "summary",
                    "content": "Old summary content"
                }
                """;

        // Act
        Response response = cvService.improveCV(jsonData);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV section improved successfully", response.getMessage());
        assertEquals("Enhanced summary with more impact and quantifiable achievements", response.getImprovedSection());
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
                experiencesDto, educationsDto, Arrays.asList("Java", "Spring"), "PUBLIC", "red", "classic");

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
                    "privacy": "PRIVATE",
                    "color": "blue",
                    "template": "modern"
                }
                """;

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
    void testAnalyzeCVWithJobDescription_Success() throws Exception {
        // Setup AI gRPC client mock for this test
        JobDescriptionResult jdResult = new JobDescriptionResult();
        jdResult.setJobTitle("Software Developer");
        jdResult.setCompany("Tech Corp");
        jdResult.setResponsibilities(Arrays.asList("Develop software", "Write tests"));
        jdResult.setRequirements(Arrays.asList("Java experience", "Spring Boot"));
        jdResult.setRequiredSkills(Arrays.asList("Java", "Spring", "SQL"));

        AIResponseDto analyzeWithJDResponse = AIResponseDto.builder()
                .analyzeResult("Excellent match for the position")
                .matchScore(92.0)
                .missingKeywords(Arrays.asList("AWS", "Microservices"))
                .jdResult(jdResult)
                .build();

        lenient().doReturn(analyzeWithJDResponse).when(aiGrpcClient).analyzeCVWithJobDescription(any(CVDto.class),
                anyString(), anyString());

        // Arrange - Use proper JSON that matches AnalyzeCVWithJDRequest structure
        String jsonData = """
                {
                    "title": "Test CV",
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
                    "jobDescription": "Looking for Java developer",
                    "language": "en"
                }
                """;

        // Act
        Response response = cvService.analyzeCVWithJobDescription(jsonData, null);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("CV analyzed with job description successfully", response.getMessage());
        assertEquals("Excellent match for the position", response.getAnalyze());
        assertEquals(92.0, response.getMatchScore());
        assertNotNull(response.getMissingKeywords());
        assertNotNull(response.getParsedJobDescription());
    }

    @Test
    void testAnalyzeCVWithJobDescription_WithFile() throws Exception {
        // Setup AI gRPC client mock for this test
        JobDescriptionResult jdResult = new JobDescriptionResult();
        jdResult.setJobTitle("Software Developer");
        jdResult.setCompany("Tech Corp");
        jdResult.setResponsibilities(Arrays.asList("Develop software", "Write tests"));
        jdResult.setRequirements(Arrays.asList("Java experience", "Spring Boot"));
        jdResult.setRequiredSkills(Arrays.asList("Java", "Spring", "SQL"));

        AIResponseDto analyzeWithJDResponse = AIResponseDto.builder()
                .analyzeResult("Excellent match for the position")
                .matchScore(92.0)
                .missingKeywords(Arrays.asList("AWS", "Microservices"))
                .jdResult(jdResult)
                .build();

        lenient().doReturn(analyzeWithJDResponse).when(aiGrpcClient).analyzeCVWithJobDescription(any(CVDto.class),
                anyString(), anyString());

        // Arrange - Create a proper JSON with jdFile field set to null
        String dataJson = """
                {
                    "title": "Test CV",
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
                    "language": "en",
                    "jdFile": null,
                    "jobDescription": "Looking for Java developer"
                }
                """;

        // Act
        Response response = cvService.analyzeCVWithJobDescription(dataJson, null);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals(92.0, response.getMatchScore());
    }

    @Test
    void testAnalyzeCVWithJobDescription_FileParsing() throws Exception {
        // Setup AI gRPC client mock for this test
        JobDescriptionResult jdResult = new JobDescriptionResult();
        jdResult.setJobTitle("Software Developer");
        jdResult.setCompany("Tech Corp");
        jdResult.setResponsibilities(Arrays.asList("Develop software", "Write tests"));
        jdResult.setRequirements(Arrays.asList("Java experience", "Spring Boot"));
        jdResult.setRequiredSkills(Arrays.asList("Java", "Spring", "SQL"));

        AIResponseDto analyzeWithJDResponse = AIResponseDto.builder()
                .analyzeResult("Excellent match for the position")
                .matchScore(92.0)
                .missingKeywords(Arrays.asList("AWS", "Microservices"))
                .jdResult(jdResult)
                .build();

        lenient().doReturn(analyzeWithJDResponse).when(aiGrpcClient).analyzeCVWithJobDescription(any(CVDto.class),
                anyString(), anyString());

        // Arrange
        MultipartFile mockJdFile = mock(MultipartFile.class);
        when(mockJdFile.isEmpty()).thenReturn(false); // Ensure the file is not empty

        AnalyzeCVWithJDRequest request = new AnalyzeCVWithJDRequest();
        request.setTitle("Test CV");

        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullname("John Doe");
        personalInfo.setEmail("john@example.com");
        request.setPersonalInfo(personalInfo);

        ExperienceDto experience = new ExperienceDto();
        experience.setCompany("Tech Corp");
        experience.setPosition("Developer");
        request.setExperiences(Arrays.asList(experience));

        EducationDto education = new EducationDto();
        education.setSchool("University");
        education.setDegree("Bachelor");
        request.setEducations(Arrays.asList(education));

        request.setSkills(Arrays.asList("Java"));
        request.setLanguage("en");
        // jdFile is passed as parameter, not in JSON
        request.setJobDescription("Looking for Java developer");

        // Removed ObjectMapper mock to let JSON parse naturally
        // when(objectMapper.readValue(anyString(),
        // any(Class.class))).thenReturn(request);
        when(jobDescriptionParserService.extractTextFromFile(mockJdFile)).thenReturn("Parsed JD content");

        // Setup AI gRPC client mock for this test
        JobDescriptionResult jdResult2 = new JobDescriptionResult();
        jdResult2.setJobTitle("Software Developer");
        jdResult2.setCompany("Tech Corp");
        jdResult2.setResponsibilities(Arrays.asList("Develop software", "Write tests"));
        jdResult2.setRequirements(Arrays.asList("Java experience", "Spring Boot"));
        jdResult2.setRequiredSkills(Arrays.asList("Java", "Spring", "SQL"));

        AIResponseDto analyzeWithJDResponse2 = AIResponseDto.builder()
                .analyzeResult("Excellent match for the position")
                .matchScore(92.0)
                .missingKeywords(Arrays.asList("AWS", "Microservices"))
                .jdResult(jdResult2)
                .build();

        lenient().doReturn(analyzeWithJDResponse2).when(aiGrpcClient).analyzeCVWithJobDescription(any(CVDto.class),
                anyString(), anyString());

        // Act
        Response response = cvService.analyzeCVWithJobDescription(
                "{\"title\":\"Test CV\",\"personalInfo\":{\"fullname\":\"John Doe\",\"email\":\"john@example.com\",\"phone\":\"1234567890\"},\"experiences\":[{\"company\":\"Tech Corp\",\"position\":\"Developer\"}],\"educations\":[{\"school\":\"University\",\"degree\":\"Bachelor\"}],\"skills\":[\"Java\"],\"language\":\"en\",\"jobDescription\":\"Looking for Java developer\"}",
                mockJdFile);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals(92.0, response.getMatchScore());
        verify(jobDescriptionParserService).extractTextFromFile(mockJdFile);
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