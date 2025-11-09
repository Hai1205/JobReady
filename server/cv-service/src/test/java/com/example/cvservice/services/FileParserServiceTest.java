package com.example.cvservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileParserServiceTest {

    @InjectMocks
    private FileParserService fileParserService;

    @Test
    void testExtractTextFromTxt_Success() throws Exception {
        // Arrange
        String content = "Test TXT Content\nName: John Doe\nEmail: john@example.com";
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes());

        // Act
        String result = fileParserService.extractTextFromFile(txtFile);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("John Doe"));
    }

    @Test
    void testExtractTextFromFile_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]);

        // Act & Assert
        assertThrows(Exception.class, () -> fileParserService.extractTextFromFile(emptyFile));
    }

    @Test
    void testExtractTextFromFile_UnsupportedFormat() {
        // Arrange
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "Test content".getBytes());

        // Act & Assert
        assertThrows(Exception.class, () -> fileParserService.extractTextFromFile(unsupportedFile));
    }
}
