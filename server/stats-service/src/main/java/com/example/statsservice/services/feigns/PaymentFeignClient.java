package com.example.statsservice.services.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.statsservice.dtos.RevenueStatsDto;

@FeignClient(name = "${PAYMENT_SERVICE_NAME}", url = "${PAYMENT_SERVICE_URL}")
public interface PaymentFeignClient {

    @GetMapping("/api/v1/payments/stats/revenue")
    RevenueStatsDto getRevenueStats();
}
