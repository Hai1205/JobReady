package com.example.cvservice.services;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.UserDto;
import com.example.cvservice.dtos.responses.Response;
import com.example.cvservice.entities.CV;
import com.example.cvservice.entities.PersonalInfo;
import com.example.cvservice.mappers.CVMapper;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.apis.CVApi;
import com.example.cvservice.services.grpcs.clients.UserGrpcClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private CVApi cvService;

    private CV mockCV;
    private CVDto mockCVDto;
    private UserDto mockUser;
    private UUID userId;
    private UUID cvId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cvId = UUID.randomUUID();

        mockCV = new CV();
        mockCV.setId(cvId);
        mockCV.setUserId(userId);
        mockCV.setTitle("Software Engineer CV");

        mockCVDto = new CVDto();
        mockCVDto.setId(cvId);
        mockCVDto.setUserId(userId);
        mockCVDto.setTitle("Software Engineer CV");

        mockUser = new UserDto();
        mockUser.setId(userId);
        mockUser.setEmail("test@example.com");
    }

    @Test
    void testCreateCV_Success() {
        // Arrange
        lenient().when(userGrpcClient.findUserById(userId)).thenReturn(mockUser);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cvRepository.save(any(CV.class))).thenReturn(mockCV);
        when(cvMapper.toDto(mockCV)).thenReturn(mockCVDto);

        // Act
        Response response = cvService.createCV(userId);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());

        verify(userGrpcClient, times(1)).findUserById(userId);
        verify(cvRepository).save(any(CV.class));
    }

    @Test
    void testCreateCV_UserNotFound() {
        // Arrange
        when(userGrpcClient.findUserById(userId)).thenReturn(null);

        // Act
        Response response = cvService.createCV(userId);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());

        verify(userGrpcClient).findUserById(userId);
        verify(cvRepository, never()).save(any(CV.class));
    }

    @Test
    void testGetCVById_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(mockCV));
        when(cvMapper.toDto(mockCV)).thenReturn(mockCVDto);

        // Act
        Response response = cvService.getCVById(cvId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        verify(cvRepository).findById(cvId);
    }

    @Test
    void testGetCVById_NotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act
        Response response = cvService.getCVById(cvId);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testHandleGetCVById_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(mockCV));
        when(cvMapper.toDto(mockCV)).thenReturn(mockCVDto);

        // Act
        CVDto result = cvService.handleGetCVById(cvId);

        // Assert
        assertNotNull(result);
        assertEquals(cvId, result.getId());

        verify(cvRepository).findById(cvId);
        verify(cvMapper).toDto(mockCV);
    }

    @Test
    void testHandleGetCVById_NotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> cvService.handleGetCVById(cvId));
    }

    @Test
    void testDeleteCV_Success() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(mockCV));

        // Act
        Response response = cvService.deleteCV(cvId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        verify(cvRepository).findById(cvId);
    }

    @Test
    void testDeleteCV_NotFound() {
        // Arrange
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

        // Act
        Response response = cvService.deleteCV(cvId);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
    }
}
