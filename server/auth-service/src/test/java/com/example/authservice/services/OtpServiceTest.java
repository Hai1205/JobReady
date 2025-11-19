package com.example.authservice.services;

import com.example.authservice.services.OtpService;
import com.example.rediscommon.services.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        // No default stubbing here, stub in individual tests as needed
    }

    @Test
    void testGenerateOtp_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        String otp = otpService.generateOtp(email);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}")); // Verify OTP contains only digits

        verify(redisService).set(eq("OTP_" + email), eq(otp), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testValidateOtp_Valid() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisService.get("OTP_" + email)).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp(email, otp);

        // Assert
        assertTrue(result);
        verify(redisService).get("OTP_" + email);
        verify(redisService).delete("OTP_" + email);
    }

    @Test
    void testValidateOtp_Invalid() {
        // Arrange
        String email = "test@example.com";
        String storedOtp = "123456";
        String providedOtp = "654321";
        when(redisService.get("OTP_" + email)).thenReturn(storedOtp);

        // Act
        boolean result = otpService.validateOtp(email, providedOtp);

        // Assert
        assertFalse(result);
        verify(redisService).get("OTP_" + email);
        verify(redisService, never()).delete(anyString());
    }

    @Test
    void testValidateOtp_Expired() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisService.get("OTP_" + email)).thenReturn(null);

        // Act
        boolean result = otpService.validateOtp(email, otp);

        // Assert
        assertFalse(result);
        verify(redisService).get("OTP_" + email);
    }

    @Test
    void testOtpExists_Exists() {
        // Arrange
        String email = "test@example.com";
        when(redisService.hasKey("OTP_" + email)).thenReturn(true);

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertTrue(result);
        verify(redisService).hasKey("OTP_" + email);
    }

    @Test
    void testOtpExists_NotExists() {
        // Arrange
        String email = "test@example.com";
        when(redisService.hasKey("OTP_" + email)).thenReturn(false);

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertFalse(result);
        verify(redisService).hasKey("OTP_" + email);
    }

    @Test
    void testOtpExists_Exception() {
        // Arrange
        String email = "test@example.com";
        when(redisService.hasKey("OTP_" + email)).thenThrow(new RuntimeException("Redis error"));

        // Act
        boolean result = otpService.otpExists(email);

        // Assert
        assertFalse(result);
        verify(redisService).hasKey("OTP_" + email);
    }

    @Test
    void testDeleteOtp_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        otpService.deleteOtp(email);

        // Assert
        verify(redisService).delete("OTP_" + email);
    }

    @Test
    void testDeleteOtp_Exception() {
        // Arrange
        String email = "test@example.com";
        doThrow(new RuntimeException("Redis error")).when(redisService).delete("OTP_" + email);

        // Act & Assert
        assertDoesNotThrow(() -> otpService.deleteOtp(email));
        verify(redisService).delete("OTP_" + email);
    }

    @Test
    void testGenerateOtp_NullEmail() {
        // Act
        String otp = otpService.generateOtp(null);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(redisService).set(eq("OTP_null"), eq(otp), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testGenerateOtp_EmptyEmail() {
        // Act
        String otp = otpService.generateOtp("");

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(redisService).set(eq("OTP_"), eq(otp), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testValidateOtp_NullEmail() {
        // Arrange
        String otp = "123456";
        when(redisService.get("OTP_null")).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp(null, otp);

        // Assert
        assertTrue(result);
        verify(redisService).get("OTP_null");
        verify(redisService).delete("OTP_null");
    }

    @Test
    void testValidateOtp_NullOtp() {
        // Arrange
        String email = "test@example.com";
        when(redisService.get("OTP_" + email)).thenReturn("123456");

        // Act
        boolean result = otpService.validateOtp(email, null);

        // Assert
        assertFalse(result);
        verify(redisService).get("OTP_" + email);
    }

    @Test
    void testValidateOtp_EmptyEmail() {
        // Arrange
        String otp = "123456";
        when(redisService.get("OTP_")).thenReturn(otp);

        // Act
        boolean result = otpService.validateOtp("", otp);

        // Assert
        assertTrue(result);
        verify(redisService).get("OTP_");
        verify(redisService).delete("OTP_");
    }

    @Test
    void testValidateOtp_EmptyOtp() {
        // Arrange
        String email = "test@example.com";
        when(redisService.get("OTP_" + email)).thenReturn("123456");

        // Act
        boolean result = otpService.validateOtp(email, "");

        // Assert
        assertFalse(result);
        verify(redisService).get("OTP_" + email);
    }

    @Test
    void testGenerateOtp_SaveOtpFails() {
        // Arrange
        String email = "test@example.com";
        doThrow(new RuntimeException("Redis save error")).when(redisService).set(anyString(), anyString(), anyLong(),
                any(TimeUnit.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> otpService.generateOtp(email));
        verify(redisService).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void testValidateOtp_RedisException() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisService.get("OTP_" + email)).thenThrow(new RuntimeException("Redis get error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> otpService.validateOtp(email, otp));
        verify(redisService).get("OTP_" + email);
    }
}
