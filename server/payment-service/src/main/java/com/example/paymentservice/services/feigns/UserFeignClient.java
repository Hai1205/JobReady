package com.example.paymentservice.services.feigns;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.paymentservice.dtos.response.Response;

@FeignClient(name = "${USER_SERVICE_NAME}", url = "${USER_SERVICE_URL}")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{userId}")
    Response getUserById(@PathVariable("userId") UUID userId);

    @PatchMapping("/api/v1/users/{userId}/plan")
    Response updateUserPlan(@PathVariable("userId") UUID userId, @RequestBody String updatePlanRequest);
}