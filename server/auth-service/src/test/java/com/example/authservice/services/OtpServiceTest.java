package com.example.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        // No default stubbing here, stub in individual tests as needed
    }

    @Test
    void testGenerateOtp_Success() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";

        // Act
        String otp = otpService.generateOtp(email);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}")); // Verify OTP contains only digits

        verify(valueOperations).set(eq("OTP_" + email), eq(otp));
        verify(redisTemplate).expire(eq("OTP_" + email), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testValidateOtp_Valid() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        String otp = "123456";
        when(valueOperations.get("OTP_" + email)).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp(email, otp);

        // Assert
        assertTrue(result);
        verify(valueOperations).get("OTP_" + email);
        verify(redisTemplate).delete("OTP_" + email);
    }

    @Test
    void testValidateOtp_Invalid() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        String storedOtp = "123456";
        String providedOtp = "654321";
        when(valueOperations.get("OTP_" + email)).thenReturn(storedOtp);

        // Act
        boolean result = otpService.validateOtp(email, providedOtp);

        // Assert
        assertFalse(result);
        verify(valueOperations).get("OTP_" + email);
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void testValidateOtp_Expired() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        String otp = "123456";
        when(valueOperations.get("OTP_" + email)).thenReturn(null);

        // Act
        boolean result = otpService.validateOtp(email, otp);

        // Assert
        assertFalse(result);
        verify(valueOperations).get("OTP_" + email);
    }

    @Test
    void testOtpExists_Exists() {
        // Arrange
        String email = "test@example.com";
        when(redisTemplate.hasKey("OTP_" + email)).thenReturn(true);

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey("OTP_" + email);
    }

    @Test
    void testOtpExists_NotExists() {
        // Arrange
        String email = "test@example.com";
        when(redisTemplate.hasKey("OTP_" + email)).thenReturn(false);

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey("OTP_" + email);
    }

    @Test
    void testOtpExists_Exception() {
        // Arrange
        String email = "test@example.com";
        when(redisTemplate.hasKey("OTP_" + email)).thenThrow(new RuntimeException("Redis error"));

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey("OTP_" + email);
    }

    @Test
    void testDeleteOtp_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        otpService.deleteOtp(email);

        // Assert
        verify(redisTemplate).delete("OTP_" + email);
    }

    @Test
    void testDeleteOtp_Exception() {
        // Arrange
        String email = "test@example.com";
        doThrow(new RuntimeException("Redis error")).when(redisTemplate).delete("OTP_" + email);

        // Act & Assert
        assertDoesNotThrow(() -> otpService.deleteOtp(email));
        verify(redisTemplate).delete("OTP_" + email);
    }

    @Test
    void testGenerateOtp_NullEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // Act
        String otp = otpService.generateOtp(null);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(valueOperations).set(eq("OTP_null"), eq(otp));
    }

    @Test
    void testGenerateOtp_EmptyEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // Act
        String otp = otpService.generateOtp("");

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(valueOperations).set(eq("OTP_"), eq(otp));
    }

    @Test
    void testValidateOtp_NullEmail() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String otp = "123456";
        when(valueOperations.get("OTP_null")).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp(null, otp);

        // Assert
        assertTrue(result);
        verify(valueOperations).get("OTP_null");
        verify(redisTemplate).delete("OTP_null");
    }

    @Test
    void testValidateOtp_NullOtp() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        when(valueOperations.get("OTP_" + email)).thenReturn("123456");

        // Act
        boolean result = otpService.validateOtp(email, null);

        // Assert
        assertFalse(result);
        verify(valueOperations).get("OTP_" + email);
    }

    @Test
    void testValidateOtp_EmptyEmail() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String otp = "123456";
        when(valueOperations.get("OTP_")).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp("", otp);

        // Assert
        assertTrue(result);
        verify(valueOperations).get("OTP_");
        verify(redisTemplate).delete("OTP_");
    }

    @Test
    void testValidateOtp_EmptyOtp() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        when(valueOperations.get("OTP_" + email)).thenReturn("123456");

        // Act
        boolean result = otpService.validateOtp(email, "");

        // Assert
        assertFalse(result);
        verify(valueOperations).get("OTP_" + email);
    }

    @Test
    void testGenerateOtp_SaveOtpFails() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        doThrow(new RuntimeException("Redis save error")).when(valueOperations).set(anyString(), anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> otpService.generateOtp(email));
        verify(valueOperations).set(anyString(), anyString());
    }

    @Test
    void testValidateOtp_RedisException() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String email = "test@example.com";
        String otp = "123456";
        when(valueOperations.get("OTP_" + email)).thenThrow(new RuntimeException("Redis get error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> otpService.validateOtp(email, otp));
        verify(valueOperations).get("OTP_" + email);
    }
}
