package com.example.statsservice.services.apis;

import com.example.rediscommon.services.RedisService;
import com.example.statsservice.dtos.ActivityDto;
import com.example.statsservice.dtos.DashboardStatsDto;
import com.example.statsservice.services.feigns.UserFeignClient;
import com.example.statsservice.services.feigns.CVFeignClient;
import com.example.statsservice.dtos.responses.Response;

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

    private final UserFeignClient userFeignClient;
    private final CVFeignClient cvFeignClient;
    private final RedisService redisService;

    @Value("${LOGO_PATH}")
    private String logoPath;

    public StatsApi(
            UserFeignClient userFeignClient,
            CVFeignClient cvFeignClient,
            RedisService redisService) {
        this.userFeignClient = userFeignClient;
        this.cvFeignClient = cvFeignClient;
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

        // Execute all Feign calls in parallel
        CompletableFuture<Response> userStatsFuture = CompletableFuture
                .supplyAsync(() -> userFeignClient.getUserStats());

        CompletableFuture<Response> totalCVsFuture = CompletableFuture
                .supplyAsync(() -> cvFeignClient.getTotalCVs());

        CompletableFuture<Response> publicCVsFuture = CompletableFuture
                .supplyAsync(() -> cvFeignClient.getCVsByVisibility(true));

        CompletableFuture<Response> privateCVsFuture = CompletableFuture
                .supplyAsync(() -> cvFeignClient.getCVsByVisibility(false));

        CompletableFuture<Response> usersThisMonthFuture = CompletableFuture
                .supplyAsync(() -> userFeignClient.getUsersCreatedInRange(startDateStr, endDateStr));

        CompletableFuture<Response> cvsThisMonthFuture = CompletableFuture
                .supplyAsync(() -> cvFeignClient.getCVsCreatedInRange(startDateStr, endDateStr));

        CompletableFuture<List<ActivityDto>> recentActivitiesFuture = CompletableFuture
                .supplyAsync(() -> handleGetRecentActivities());

        // Wait for all futures to complete and get results
        Response userStatsResponse = userStatsFuture.join();
        Response totalCVsResponse = totalCVsFuture.join();
        Response publicCVsResponse = publicCVsFuture.join();
        Response privateCVsResponse = privateCVsFuture.join();
        Response usersThisMonthResponse = usersThisMonthFuture.join();
        Response cvsThisMonthResponse = cvsThisMonthFuture.join();
        List<ActivityDto> recentActivities = recentActivitiesFuture.join();

        // Build dashboard stats
        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalUsers(toLong(userStatsResponse.getAdditionalData().get("totalUsers")))
                .activeUsers(toLong(userStatsResponse.getAdditionalData().get("activeUsers")))
                .pendingUsers(toLong(userStatsResponse.getAdditionalData().get("pendingUsers")))
                .bannedUsers(toLong(userStatsResponse.getAdditionalData().get("bannedUsers")))
                .usersCreatedThisMonth(toLong(usersThisMonthResponse.getAdditionalData().get("count")))
                .totalCVs(toLong(totalCVsResponse.getAdditionalData().get("total")))
                .publicCVs(toLong(publicCVsResponse.getAdditionalData().get("count")))
                .privateCVs(toLong(privateCVsResponse.getAdditionalData().get("count")))
                .cvsCreatedThisMonth(toLong(cvsThisMonthResponse.getAdditionalData().get("count")))
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
            byte[] report = handleGetStatsReport();

            response.setStatusCode(200);
            response.setMessage("Dashboard statistics retrieved successfully");
            response.setDashboardStats(stats);
            response.setStatsReport(report);
            return response;
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
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Failed to fetch dashboard statistics");
        }
    }

    private List<ActivityDto> handleGetRecentActivities() {
        List<ActivityDto> activities = new ArrayList<>();

        try {
            // Get recent users
            Response recentUsersResponse = userFeignClient.getRecentUsers(5);

            // Add user registration activities
            for (Map<String, Object> user : recentUsersResponse.getUsers()) {
                activities.add(ActivityDto.builder()
                        .id("user-" + user.get("id").toString())
                        .type("user_registered")
                        .description("New user registered: " + (String) user.get("fullname"))
                        .timestamp(Instant.now().toString()) // Since we don't have created_at
                        .userId(user.get("id").toString())
                        .build());
            }

            // Get recent CVs
            Response recentCVsResponse = cvFeignClient.getRecentCVs(5);

            // Add CV creation activities
            for (Map<String, Object> cv : recentCVsResponse.getCvs()) {
                activities.add(ActivityDto.builder()
                        .id("cv-" + cv.get("id").toString())
                        .type("cv_created")
                        .description("New CV created: " + (String) cv.get("title"))
                        .timestamp((String) cv.get("createdAt"))
                        .userId(cv.get("userId").toString())
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

    /**
     * Safely convert Number to Long
     */
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Cannot convert {} to Long, returning 0", value);
            return 0L;
        }
    }
}
