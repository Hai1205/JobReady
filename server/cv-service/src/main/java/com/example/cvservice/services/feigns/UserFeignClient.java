package com.example.cvservice.services.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.cvservice.dtos.responses.Response;

@FeignClient(name = "${USER_SERVICE_NAME}", url = "${USER_SERVICE_URL}")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{userId}")
    Response getUserById(@PathVariable("userId") String userId);
}