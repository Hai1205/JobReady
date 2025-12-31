package com.example.paymentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO cho thống kê doanh thu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDto {
    
    // Tổng doanh thu
    private BigDecimal totalRevenue;
    
    // Doanh thu tháng này
    private BigDecimal thisMonthRevenue;
    
    // Doanh thu tháng trước
    private BigDecimal lastMonthRevenue;
    
    // Tỷ lệ tăng trưởng so với tháng trước (%)
    private Double growthRate;
    
    // Số lượng giao dịch thành công
    private Long successfulTransactions;
    
    // Số lượng giao dịch thất bại
    private Long failedTransactions;
    
    // Số lượng giao dịch đang chờ
    private Long pendingTransactions;
    
    // Doanh thu theo phương thức thanh toán
    private Map<String, BigDecimal> revenueByPaymentMethod;
    
    // Doanh thu theo gói (plan)
    private Map<String, BigDecimal> revenueByPlan;
    
    // Doanh thu theo ngày trong tháng này (30 ngày gần nhất)
    private List<DailyRevenueDto> dailyRevenue;
    
    // Doanh thu theo tháng (12 tháng gần nhất)
    private List<MonthlyRevenueDto> monthlyRevenue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenueDto {
        private String date; // yyyy-MM-dd
        private BigDecimal revenue;
        private Long transactions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueDto {
        private String month; // yyyy-MM
        private BigDecimal revenue;
        private Long transactions;
    }
}
