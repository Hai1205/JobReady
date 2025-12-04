package com.example.authservice.dtos.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerifyOtpRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        VerifyOtpRequest request = new VerifyOtpRequest();
        String otp = "123456";

        // Act
        request.setOtp(otp);
        request.setIsActivation(Boolean.TRUE);

        // Assert
        assertEquals(otp, request.getOtp());
        assertTrue(request.getIsActivation());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        VerifyOtpRequest request = new VerifyOtpRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getOtp());
        assertNull(request.getIsActivation());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        VerifyOtpRequest request = new VerifyOtpRequest("123456", Boolean.TRUE);

        // Assert
        assertNotNull(request);
        assertEquals("123456", request.getOtp());
        assertTrue(request.getIsActivation());
    }

    @Test
    void testBuilder() {
        // Act
        VerifyOtpRequest request = VerifyOtpRequest.builder()
            .otp("123456")
            .isActivation(Boolean.TRUE)
            .build();

        // Assert
        assertNotNull(request);
        assertEquals("123456", request.getOtp());
        assertTrue(request.getIsActivation());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        VerifyOtpRequest request1 = new VerifyOtpRequest("123456", Boolean.TRUE);
        VerifyOtpRequest request2 = new VerifyOtpRequest("123456", Boolean.TRUE);
        VerifyOtpRequest request3 = new VerifyOtpRequest("654321", Boolean.FALSE);

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        VerifyOtpRequest request = new VerifyOtpRequest("123456", Boolean.TRUE);

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("123456"));
    }
}
