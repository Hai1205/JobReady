package com.example.statsservice.services.apis;

import com.example.grpc.user.*;
import com.example.grpc.cv.*;
import com.example.rediscommon.services.RedisService;
import com.example.statsservice.dtos.ActivityDto;
import com.example.statsservice.dtos.DashboardStatsDto;
import com.example.statsservice.dtos.reponses.Response;
import com.example.statsservice.services.grpcs.clients.CVGrpcClient;
import com.example.statsservice.services.grpcs.clients.UserGrpcClient;

import io.grpc.StatusRuntimeException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class StatsApi extends BaseApi {

    private final UserGrpcClient userGrpcClient;
    private final CVGrpcClient cvGrpcClient;
    private final RedisService redisService;

    @Value("${LOGO_PATH}")
    private String logoPath;

    public StatsApi(
            UserGrpcClient userGrpcClient,
            CVGrpcClient cvGrpcClient,
            RedisService redisService) {
        this.userGrpcClient = userGrpcClient;
        this.cvGrpcClient = cvGrpcClient;
        this.redisService = redisService;
    }

    /**
     * Get dashboard statistics from cache or compute if not available
     */
    private DashboardStatsDto getDashboardStatsFromCacheOrCompute() {
        String cacheKey = "dashboard_stats";

        // Try to get from cache first
        if (redisService.hasKey(cacheKey)) {
            logger.info("Retrieving dashboard statistics from Redis cache");
            try {
                return (DashboardStatsDto) redisService.get(cacheKey);
            } catch (Exception e) {
                logger.warn("Failed to retrieve dashboard stats from cache, computing fresh data: {}", e.getMessage());
            }
        }

        // Compute fresh data
        logger.info("Computing fresh dashboard statistics");
        DashboardStatsDto stats = computeDashboardStats();

        // Cache the result for 10 minutes
        try {
            redisService.set(cacheKey, stats, 10, TimeUnit.MINUTES);
            logger.info("Dashboard statistics cached in Redis for 10 minutes");
        } catch (Exception e) {
            logger.warn("Failed to cache dashboard stats: {}", e.getMessage());
        }

        return stats;
    }

    /**
     * Compute dashboard statistics from gRPC calls (optimized with parallel
     * execution)
     */
    private DashboardStatsDto computeDashboardStats() {
        logger.info("Fetching dashboard statistics with parallel execution...");

        // Get this month's date range
        YearMonth currentMonth = YearMonth.now();
        Instant startOfMonth = currentMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        String startDateStr = startOfMonth.toString();
        String endDateStr = endOfMonth.toString();

        // Execute all gRPC calls in parallel
        CompletableFuture<GetUserStatsResponse> userStatsFuture = CompletableFuture
                .supplyAsync(() -> userGrpcClient.getUserStats());

        CompletableFuture<GetTotalCVsResponse> totalCVsFuture = CompletableFuture
                .supplyAsync(() -> cvGrpcClient.getTotalCVs());

        CompletableFuture<GetCVsByVisibilityResponse> publicCVsFuture = CompletableFuture
                .supplyAsync(() -> cvGrpcClient.getCVsByVisibility(true));

        CompletableFuture<GetCVsByVisibilityResponse> privateCVsFuture = CompletableFuture
                .supplyAsync(() -> cvGrpcClient.getCVsByVisibility(false));

        CompletableFuture<GetUsersCreatedInRangeResponse> usersThisMonthFuture = CompletableFuture
                .supplyAsync(() -> userGrpcClient.getUsersCreatedInRange(startDateStr, endDateStr));

        CompletableFuture<GetCVsCreatedInRangeResponse> cvsThisMonthFuture = CompletableFuture
                .supplyAsync(() -> cvGrpcClient.getCVsCreatedInRange(startDateStr, endDateStr));

        CompletableFuture<List<ActivityDto>> recentActivitiesFuture = CompletableFuture
                .supplyAsync(() -> handleGetRecentActivities());

        // Wait for all futures to complete and get results
        GetUserStatsResponse userStatsResponse = userStatsFuture.join();
        GetTotalCVsResponse totalCVsResponse = totalCVsFuture.join();
        GetCVsByVisibilityResponse publicCVsResponse = publicCVsFuture.join();
        GetCVsByVisibilityResponse privateCVsResponse = privateCVsFuture.join();
        GetUsersCreatedInRangeResponse usersThisMonthResponse = usersThisMonthFuture.join();
        GetCVsCreatedInRangeResponse cvsThisMonthResponse = cvsThisMonthFuture.join();
        List<ActivityDto> recentActivities = recentActivitiesFuture.join();

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

    public byte[] handleGetStatsReport() throws Exception {
        logger.info("Generating dashboard statistics report...");

        // Fetch dashboard stats from your service
        DashboardStatsDto dashboardStats = getDashboardStatsFromCacheOrCompute();

        // Load the report template
        ClassPathResource templateResource = new ClassPathResource("reports/dashboard-report.jrxml");
        InputStream reportStream = templateResource.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Set report parameters - Cast long to Integer for JasperReports compatibility
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("LOGO_PATH", logoPath);
        parameters.put("REPORT_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        parameters.put("TOTAL_USERS", (int) dashboardStats.getTotalUsers());
        parameters.put("ACTIVE_USERS", (int) dashboardStats.getActiveUsers());
        parameters.put("PENDING_USERS", (int) dashboardStats.getPendingUsers());
        parameters.put("BANNED_USERS", (int) dashboardStats.getBannedUsers());
        parameters.put("USERS_CREATED_THIS_MONTH", (int) dashboardStats.getUsersCreatedThisMonth());
        parameters.put("TOTAL_CVS", (int) dashboardStats.getTotalCVs());
        parameters.put("PUBLIC_CVS", (int) dashboardStats.getPublicCVs());
        parameters.put("PRIVATE_CVS", (int) dashboardStats.getPrivateCVs());
        parameters.put("CVS_CREATED_THIS_MONTH", (int) dashboardStats.getCvsCreatedThisMonth());

        // Create JRBeanCollectionDataSource from activities
        JRDataSource dataSource = new JRBeanCollectionDataSource(
                dashboardStats.getRecentActivities() != null ? dashboardStats.getRecentActivities()
                        : Collections.emptyList());

        // Compile and fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        logger.info("Dashboard statistics report generated successfully");
        return pdfBytes;
    }

    public Response getDashboardStats() {
        Response response = new Response();

        try {
            DashboardStatsDto stats = getDashboardStatsFromCacheOrCompute();

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

    public Response getStatsReport() {
        Response response = new Response();

        try {
            byte[] report = handleGetStatsReport();

            response.setStatusCode(200);
            response.setMessage("Stats report retrieved successfully");
            response.setStatsReport(report);
            return response;
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error fetching dashboard stats: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Failed to fetch dashboard statistics: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Failed to fetch dashboard statistics");
        }
    }

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

            // Convert subList to ArrayList to avoid Redis serialization issues
            int limit = Math.min(10, activities.size());
            return new ArrayList<>(activities.subList(0, limit));
        } catch (Exception e) {
            logger.warn("Error fetching recent activities: {}", e.getMessage());
            return activities; // Return partial results
        }
    }
}
