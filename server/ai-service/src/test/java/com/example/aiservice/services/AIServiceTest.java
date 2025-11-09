package com.example.aiservice.services;

import com.example.aiservice.configs.OpenRouterConfig;
import com.example.aiservice.dtos.*;
import com.example.aiservice.exceptions.OurException;
import com.example.aiservice.services.apis.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private OpenRouterConfig openRouterConfig;

    @Mock
    private PromptBuilderService promptBuilderService;

    @InjectMocks
    private AIService aiService;

    private CVDto mockCVDto;
    private PersonalInfoDto mockPersonalInfo;

    @BeforeEach
    void setUp() {
        // Setup mock personal info
        mockPersonalInfo = new PersonalInfoDto(
                null, // id
                "John Doe", // fullname
                "john.doe@example.com", // email
                "+1234567890", // phone
                "New York, USA", // location
                "Experienced Software Engineer with 5+ years in Java development", // summary
                null, // avatarUrl
                null // avatarPublicId
        );

        // Setup mock experience
        ExperienceDto mockExperience = new ExperienceDto(
                null, // id
                "Senior Software Engineer", // position
                "Tech Corp", // company
                "2020-01", // startDate
                "2024-11", // endDate
                "Developed microservices using Spring Boot and Java" // description
        );

        // Setup mock education
        EducationDto mockEducation = new EducationDto(
                null, // id
                "Bachelor of Science", // degree
                "Computer Science", // field
                "University of Technology", // school
                "2015-09", // startDate
                "2019-06" // endDate
        );

        // Setup mock CV
        mockCVDto = CVDto.builder()
                .personalInfo(mockPersonalInfo)
                .experiences(Arrays.asList(mockExperience))
                .educations(Arrays.asList(mockEducation))
                .skills(Arrays.asList("Java", "Spring Boot", "Microservices", "REST API"))
                .build();
    }

    @Test
    void testAnalyzeCV_Success() {
        // Arrange
        String mockPrompt = "Analyze CV system prompt";
        String mockAIResponse = """
                {
                    "suggestions": [
                        {
                            "id": "1",
                            "type": "improvement",
                            "section": "experience",
                            "message": "Add more metrics",
                            "suggestion": "Quantify your achievements"
                        }
                    ]
                }
                """;

        when(promptBuilderService.buildCVAnalysisPrompt()).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCV(mockCVDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnalyzeResult());
        assertNotNull(result.getSuggestions());
        assertEquals(1, result.getSuggestions().size());
        assertEquals("improvement", result.getSuggestions().get(0).getType());
        assertEquals("experience", result.getSuggestions().get(0).getSection());

        verify(promptBuilderService, times(1)).buildCVAnalysisPrompt();
        verify(openRouterConfig, times(1)).callModelWithSystemPrompt(eq(mockPrompt), anyString());
    }

    @Test
    void testAnalyzeCV_ThrowsOurException() {
        // Arrange
        when(promptBuilderService.buildCVAnalysisPrompt()).thenThrow(new OurException("AI service error", 500));

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.analyzeCV(mockCVDto);
        });

        assertEquals("AI service error", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testImproveCV_Success() {
        // Arrange
        String section = "experience";
        String content = "Worked on various projects";
        String mockPrompt = "Improve CV system prompt";
        String mockImprovedContent = "Led and delivered 5+ enterprise projects using Spring Boot and microservices architecture";

        when(promptBuilderService.buildCVImprovementPrompt(anyString(), anyString(), anyList())).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockImprovedContent);

        // Act
        AIResponseDto result = aiService.improveCV(section, content);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getImproved());
        assertTrue(result.getImproved().contains("Led and delivered"));

        verify(promptBuilderService, times(1)).buildCVImprovementPrompt(eq(section), eq("General position"), anyList());
        verify(openRouterConfig, times(1)).callModelWithSystemPrompt(eq(mockPrompt), anyString());
    }

    @Test
    void testImproveCV_ThrowsException() {
        // Arrange
        String section = "experience";
        String content = "Test content";
        when(promptBuilderService.buildCVImprovementPrompt(anyString(), anyString(), anyList()))
                .thenReturn("prompt");
        when(openRouterConfig.callModelWithSystemPrompt(anyString(), anyString()))
                .thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.improveCV(section, content);
        });

        assertEquals("Failed to improve CV", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testAnalyzeCVWithJobDescription_Success() {
        // Arrange
        String jdText = "We are looking for a Senior Java Developer with Spring Boot experience";
        String language = "en";
        String mockPrompt = "Job match system prompt";
        String mockAIResponse = """
                {
                    "jobTitle": "Senior Java Developer",
                    "overallMatchScore": 85.5,
                    "missingKeywords": ["Docker", "Kubernetes"],
                    "suggestions": [
                        {
                            "id": "1",
                            "type": "improvement",
                            "section": "skills",
                            "message": "Add container technologies",
                            "suggestion": "Include Docker and Kubernetes in your skills"
                        }
                    ]
                }
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq(language))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnalyzeResult());
        assertNotNull(result.getMatchScore());
        assertEquals(85.5, result.getMatchScore());
        assertNotNull(result.getMissingKeywords());
        assertEquals(2, result.getMissingKeywords().size());
        assertTrue(result.getMissingKeywords().contains("Docker"));
        assertTrue(result.getMissingKeywords().contains("Kubernetes"));
        assertNotNull(result.getSuggestions());
        assertEquals(1, result.getSuggestions().size());

        verify(promptBuilderService, times(1)).buildJobMatchPrompt(eq(language));
        verify(openRouterConfig, times(1)).callModelWithSystemPrompt(eq(mockPrompt), anyString());
    }

    @Test
    void testAnalyzeCVWithJobDescription_DefaultLanguage() {
        // Arrange
        String jdText = "Tìm kiếm Senior Java Developer";
        String mockPrompt = "Job match system prompt";
        String mockAIResponse = """
                {
                    "jobTitle": "Senior Java Developer",
                    "matchScore": 90.0,
                    "missingKeywords": [],
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq("vi"))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, null, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(90.0, result.getMatchScore());
        verify(promptBuilderService, times(1)).buildJobMatchPrompt(eq("vi"));
    }

    @Test
    void testAnalyzeCVWithJobDescription_ParsesMarkdownResponse() {
        // Arrange
        String jdText = "Job description";
        String language = "en";
        String mockPrompt = "Job match prompt";
        String mockAIResponse = """
                ```json
                {
                    "jobTitle": "Developer",
                    "overallMatchScore": 75.0,
                    "missingKeywords": ["AWS"],
                    "suggestions": []
                }
                ```
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq(language))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(75.0, result.getMatchScore());
        assertEquals(1, result.getMissingKeywords().size());
        assertTrue(result.getMissingKeywords().contains("AWS"));
    }

    @Test
    void testAnalyzeCVWithJobDescription_ThrowsException() {
        // Arrange
        String jdText = "Job description";
        String language = "en";
        when(promptBuilderService.buildJobMatchPrompt(anyString())).thenReturn("prompt");
        when(openRouterConfig.callModelWithSystemPrompt(anyString(), anyString()))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);
        });

        assertEquals("Failed to analyze CV with Job Description", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testAnalyzeCV_WithEmptySuggestions() {
        // Arrange
        String mockPrompt = "Analyze CV prompt";
        String mockAIResponse = """
                {
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildCVAnalysisPrompt()).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCV(mockCVDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSuggestions());
        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    void testAnalyzeCV_WithNoPersonalInfo() {
        // Arrange
        CVDto cvWithoutPersonalInfo = CVDto.builder()
                .experiences(mockCVDto.getExperiences())
                .educations(mockCVDto.getEducations())
                .skills(mockCVDto.getSkills())
                .build();

        String mockPrompt = "Analyze CV prompt";
        String mockAIResponse = """
                {
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildCVAnalysisPrompt()).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCV(cvWithoutPersonalInfo);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnalyzeResult());
    }

    @Test
    void testAnalyzeCVWithJobDescription_WithParsedJobDescription() {
        // Arrange
        String jdText = "Job description";
        String language = "en";
        String mockPrompt = "Job match prompt";
        String mockAIResponse = """
                {
                    "parsedJobDescription": {
                        "jobTitle": "Software Engineer",
                        "responsibilities": ["Develop features", "Write tests"]
                    },
                    "overallMatchScore": 88.0,
                    "missingKeywords": [],
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq(language))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(88.0, result.getMatchScore());
        assertNotNull(result.getJdResult());
    }

    @Test
    void testAnalyzeCV_WithMalformedJSON() {
        // Arrange
        String mockPrompt = "Analyze CV prompt";
        String mockAIResponse = "This is not valid JSON response";

        when(promptBuilderService.buildCVAnalysisPrompt()).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCV(mockCVDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSuggestions());
        assertTrue(result.getSuggestions().isEmpty()); // Should handle gracefully
    }

    @Test
    void testImproveCV_WithEmptyContent() {
        // Arrange
        String section = "experience";
        String content = "";
        String mockPrompt = "Improve CV prompt";
        String mockImprovedContent = "Please provide more details about your experience";

        when(promptBuilderService.buildCVImprovementPrompt(anyString(), anyString(), anyList())).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockImprovedContent);

        // Act
        AIResponseDto result = aiService.improveCV(section, content);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getImproved());
    }

    @Test
    void testAnalyzeCVWithJobDescription_WithMatchScoreAlternativeField() {
        // Arrange
        String jdText = "Job description";
        String language = "en";
        String mockPrompt = "Job match prompt";
        String mockAIResponse = """
                {
                    "jobTitle": "Developer",
                    "matchScore": 82.5,
                    "missingKeywords": [],
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq(language))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(82.5, result.getMatchScore());
    }

    @Test
    void testAnalyzeCV_NullCV() {
        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.analyzeCV(null);
        });

        assertEquals("Failed to analyze CV", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testImproveCV_NullSection() {
        // Arrange
        String content = "Test content";
        String mockPrompt = "Improve CV prompt";
        String mockImprovedContent = "Improved content";

        doReturn(mockPrompt).when(promptBuilderService).buildCVImprovementPrompt(any(), any(), anyList());
        doReturn(mockImprovedContent).when(openRouterConfig).callModelWithSystemPrompt(anyString(), anyString());

        // Act
        AIResponseDto result = aiService.improveCV(null, content);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getImproved());
        verify(promptBuilderService, times(1)).buildCVImprovementPrompt(eq(null), eq("General position"), anyList());
        verify(openRouterConfig, times(1)).callModelWithSystemPrompt(eq(mockPrompt), anyString());
    }

    @Test
    void testImproveCV_NullContent() {
        // Arrange
        String section = "experience";
        String mockPrompt = "Improve CV prompt";
        String mockImprovedContent = "Improved content";

        doReturn(mockPrompt).when(promptBuilderService).buildCVImprovementPrompt(any(), any(), anyList());
        doReturn(mockImprovedContent).when(openRouterConfig).callModelWithSystemPrompt(anyString(), anyString());

        // Act
        AIResponseDto result = aiService.improveCV(section, null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getImproved());
        verify(promptBuilderService, times(1)).buildCVImprovementPrompt(eq(section), eq("General position"), anyList());
        verify(openRouterConfig, times(1)).callModelWithSystemPrompt(eq(mockPrompt), anyString());
    }

    @Test
    void testAnalyzeCVWithJobDescription_NullCV() {
        // Arrange
        String jdText = "Job description";
        String language = "en";

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.analyzeCVWithJobDescription(null, language, jdText);
        });

        assertEquals("Failed to analyze CV with Job Description", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testAnalyzeCVWithJobDescription_NullJdText() {
        // Arrange
        String language = "en";

        // Act & Assert
        OurException exception = assertThrows(OurException.class, () -> {
            aiService.analyzeCVWithJobDescription(mockCVDto, language, null);
        });

        assertEquals("Failed to analyze CV with Job Description", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testAnalyzeCVWithJobDescription_EmptyJdText() {
        // Arrange
        String jdText = "";
        String language = "en";
        String mockPrompt = "Job match prompt";
        String mockAIResponse = """
                {
                    "jobTitle": "Developer",
                    "overallMatchScore": 70.0,
                    "missingKeywords": [],
                    "suggestions": []
                }
                """;

        when(promptBuilderService.buildJobMatchPrompt(eq(language))).thenReturn(mockPrompt);
        when(openRouterConfig.callModelWithSystemPrompt(eq(mockPrompt), anyString())).thenReturn(mockAIResponse);

        // Act
        AIResponseDto result = aiService.analyzeCVWithJobDescription(mockCVDto, language, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(70.0, result.getMatchScore());
    }
}
