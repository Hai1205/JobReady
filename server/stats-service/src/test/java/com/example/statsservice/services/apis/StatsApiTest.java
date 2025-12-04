package com.example.statsservice.services.apis;

import com.example.rediscommon.services.RedisService;
import com.example.statsservice.dtos.responses.Response;
import com.example.statsservice.services.feigns.CVFeignClient;
import com.example.statsservice.services.feigns.UserFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class StatsApiTest {

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private CVFeignClient cvFeignClient;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private StatsApi statsApi;

    @BeforeEach
    void setUp() {
        // ReflectionTestUtils.setField(statsApi, "logoPath", "/path/to/logo");
    }

    @Test
    void testGetDashboardStats_Success() {
        // Mock Redis to return null (not cached)
        when(redisService.hasKey("dashboard_stats")).thenReturn(false);

        // Mock Feign clients
        Response userStatsResponse = new Response(200, "Success");
        userStatsResponse
                .setAdditionalData(Map.of("totalUsers", 10L, "activeUsers", 8L, "pendingUsers", 1L, "bannedUsers", 1L));
        when(userFeignClient.getUserStats()).thenReturn(userStatsResponse);

        Response totalCVsResponse = new Response(200, "Success");
        totalCVsResponse.setAdditionalData(Map.of("total", 5L));
        when(cvFeignClient.getTotalCVs()).thenReturn(totalCVsResponse);

        Response publicCVsResponse = new Response(200, "Success");
        publicCVsResponse.setAdditionalData(Map.of("count", 3L));
        when(cvFeignClient.getCVsByVisibility(true)).thenReturn(publicCVsResponse);

        Response privateCVsResponse = new Response(200, "Success");
        privateCVsResponse.setAdditionalData(Map.of("count", 2L));
        when(cvFeignClient.getCVsByVisibility(false)).thenReturn(privateCVsResponse);

        Response usersThisMonthResponse = new Response(200, "Success");
        usersThisMonthResponse.setAdditionalData(Map.of("count", 2L));
        when(userFeignClient.getUsersCreatedInRange(anyString(), anyString())).thenReturn(usersThisMonthResponse);

        Response cvsThisMonthResponse = new Response(200, "Success");
        cvsThisMonthResponse.setAdditionalData(Map.of("count", 1L));
        when(cvFeignClient.getCVsCreatedInRange(anyString(), anyString())).thenReturn(cvsThisMonthResponse);

        Response response = statsApi.getDashboardStats();

        assertEquals(200, response.getStatusCode());
        assertEquals("Dashboard statistics retrieved successfully", response.getMessage());
        assertNotNull(response.getDashboardStats());
    }

    @Test
    void testGetStatsReport_Success() {
        // Mock similar to above
        when(redisService.hasKey("dashboard_stats")).thenReturn(false);

        Response userStatsResponse = new Response(200, "Success");
        userStatsResponse
                .setAdditionalData(Map.of("totalUsers", 10L, "activeUsers", 8L, "pendingUsers", 1L, "bannedUsers", 1L));
        when(userFeignClient.getUserStats()).thenReturn(userStatsResponse);

        Response totalCVsResponse = new Response(200, "Success");
        totalCVsResponse.setAdditionalData(Map.of("total", 5L));
        when(cvFeignClient.getTotalCVs()).thenReturn(totalCVsResponse);

        Response publicCVsResponse = new Response(200, "Success");
        publicCVsResponse.setAdditionalData(Map.of("count", 3L));
        when(cvFeignClient.getCVsByVisibility(true)).thenReturn(publicCVsResponse);

        Response privateCVsResponse = new Response(200, "Success");
        privateCVsResponse.setAdditionalData(Map.of("count", 2L));
        when(cvFeignClient.getCVsByVisibility(false)).thenReturn(privateCVsResponse);

        Response usersThisMonthResponse = new Response(200, "Success");
        usersThisMonthResponse.setAdditionalData(Map.of("count", 2L));
        when(userFeignClient.getUsersCreatedInRange(anyString(), anyString())).thenReturn(usersThisMonthResponse);

        Response cvsThisMonthResponse = new Response(200, "Success");
        cvsThisMonthResponse.setAdditionalData(Map.of("count", 1L));
        when(cvFeignClient.getCVsCreatedInRange(anyString(), anyString())).thenReturn(cvsThisMonthResponse);

        Response response = statsApi.getStatsReport();

        assertEquals(200, response.getStatusCode());
        assertEquals("Stats report retrieved successfully", response.getMessage());
        assertNotNull(response.getStatsReport());
    }

    @Test
    void testGetDashboardStats_Exception() {
        when(redisService.hasKey("dashboard_stats")).thenThrow(new RuntimeException("Redis error"));

        Response response = statsApi.getDashboardStats();

        assertEquals(500, response.getStatusCode());
        assertEquals("Failed to fetch dashboard statistics", response.getMessage());
    }
}