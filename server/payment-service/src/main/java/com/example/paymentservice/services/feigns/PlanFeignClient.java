package com.example.paymentservice.services.feigns;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.paymentservice.dtos.response.Response;


@FeignClient(name = "${USER_SERVICE_NAME}", url = "${USER_SERVICE_URL}")
public interface PlanFeignClient {

    @GetMapping("/api/v1/plans/{planId}")
    Response getPlanById(@PathVariable("planId") UUID planId);
}