package com.example.statsservice.services.apis;

import com.example.grpc.user.*;
import com.example.grpc.cv.*;
import com.example.statsservice.dtos.ActivityDto;
import com.example.statsservice.dtos.DashboardStatsDto;
import com.example.statsservice.dtos.reponses.Response;
import com.example.statsservice.services.grpcs.clients.CVGrpcClient;
import com.example.statsservice.services.grpcs.clients.UserGrpcClient;

import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsApi extends BaseApi {

    private final UserGrpcClient userGrpcClient;
    private final CVGrpcClient cvGrpcClient;

    public StatsApi(
            UserGrpcClient userGrpcClient,
            CVGrpcClient cvGrpcClient) {
        this.userGrpcClient = userGrpcClient;
        this.cvGrpcClient = cvGrpcClient;
    }

    /**
     * Handle get dashboard statistics - internal logic
     */
    public DashboardStatsDto handleGetDashboardStats() {
        logger.info("Fetching dashboard statistics...");

        // Get user statistics
        GetUserStatsResponse userStatsResponse = userGrpcClient.getUserStats();

        // Get CV statistics
        GetTotalCVsResponse totalCVsResponse = cvGrpcClient.getTotalCVs();
        GetCVsByVisibilityResponse publicCVsResponse = cvGrpcClient.getCVsByVisibility(true);
        GetCVsByVisibilityResponse privateCVsResponse = cvGrpcClient.getCVsByVisibility(false);

        // Get this month's date range
        YearMonth currentMonth = YearMonth.now();
        Instant startOfMonth = currentMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        // Get users created this month
        GetUsersCreatedInRangeResponse usersThisMonthResponse = userGrpcClient.getUsersCreatedInRange(
                startOfMonth.toString(),
                endOfMonth.toString());

        // Get CVs created this month
        GetCVsCreatedInRangeResponse cvsThisMonthResponse = cvGrpcClient.getCVsCreatedInRange(
                startOfMonth.toString(),
                endOfMonth.toString());

        // Get recent activities
        List<ActivityDto> recentActivities = handleGetRecentActivities();

        // Build dashboard stats
        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalUsers(userStatsResponse.getTotalUsers())
                .activeUsers(userStatsResponse.getActiveUsers())
                .pendingUsers(userStatsResponse.getPendingUsers())
                .bannedUsers(userStatsResponse.getBannedUsers())
                .usersCreatedThisMonth(usersThisMonthResponse.getCount())
                .totalCVs(totalCVsResponse.getTotal())
                .publicCVs(publicCVsResponse.getCount())
                .privateCVs(privateCVsResponse.getCount())
                .cvsCreatedThisMonth(cvsThisMonthResponse.getCount())
                .recentActivities(recentActivities)
                .build();

        logger.info("Dashboard statistics fetched successfully");
        return stats;
    }

    /**
     * Get dashboard statistics - API endpoint method
     */
    public Response getDashboardStats() {
        Response response = new Response();

        try {
            DashboardStatsDto stats = handleGetDashboardStats();

            response.setStatusCode(200);
            response.setMessage("Dashboard statistics retrieved successfully");
            response.setDashboardStats(stats);
            return response;
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error fetching dashboard stats: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Failed to fetch dashboard statistics: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Failed to fetch dashboard statistics");
        }
    }

    /**
     * Get recent activities by combining recent users and CVs - internal logic
     */
    private List<ActivityDto> handleGetRecentActivities() {
        List<ActivityDto> activities = new ArrayList<>();

        try {
            // Get recent users
            GetRecentUsersResponse recentUsersResponse = userGrpcClient.getRecentUsers(5);

            // Add user registration activities
            for (UserInfo user : recentUsersResponse.getUsersList()) {
                activities.add(ActivityDto.builder()
                        .id("user-" + user.getId())
                        .type("user_registered")
                        .description("New user registered: " + user.getFullname())
                        .timestamp(Instant.now().toString()) // Since we don't have created_at
                        .userId(user.getId())
                        .build());
            }

            // Get recent CVs
            GetRecentCVsResponse recentCVsResponse = cvGrpcClient.getRecentCVs(5);

            // Add CV creation activities
            for (CVInfo cv : recentCVsResponse.getCvsList()) {
                activities.add(ActivityDto.builder()
                        .id("cv-" + cv.getId())
                        .type("cv_created")
                        .description("New CV created: " + cv.getTitle())
                        .timestamp(cv.getCreatedAt())
                        .userId(cv.getUserId())
                        .build());
            }

            // Sort by timestamp descending and limit to 10
            activities.sort((a, b) -> {
                try {
                    Instant timeA = Instant.parse(a.getTimestamp());
                    Instant timeB = Instant.parse(b.getTimestamp());
                    return timeB.compareTo(timeA);
                } catch (Exception e) {
                    return 0;
                }
            });

            return activities.subList(0, Math.min(10, activities.size()));
        } catch (Exception e) {
            logger.warn("Error fetching recent activities: {}", e.getMessage());
            return activities; // Return partial results
        }
    }
}
