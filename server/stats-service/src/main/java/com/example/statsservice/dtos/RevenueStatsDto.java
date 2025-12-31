package com.example.statsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO cho thống kê doanh thu (copy từ payment-service)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDto {

    private BigDecimal totalRevenue;
    private BigDecimal thisMonthRevenue;
    private BigDecimal lastMonthRevenue;
    private Double growthRate;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long pendingTransactions;
    private Map<String, BigDecimal> revenueByPaymentMethod;
    private Map<String, BigDecimal> revenueByPlan;
    private List<DailyRevenueDto> dailyRevenue;
    private List<MonthlyRevenueDto> monthlyRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenueDto {
        private String date;
        private BigDecimal revenue;
        private Long transactions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueDto {
        private String month;
        private BigDecimal revenue;
        private Long transactions;
    }
}
