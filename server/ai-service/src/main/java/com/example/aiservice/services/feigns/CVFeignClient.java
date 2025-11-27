package com.example.aiservice.services.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aiservice.dtos.responses.Response;

@FeignClient(name = "${CV_SERVICE_NAME}", url = "${CV_SERVICE_URL}")
public interface CVFeignClient {

    @PostMapping("/api/v1/cvs/import/users/{userId}")
    Response importCV(@PathVariable("userId") String userId, @RequestBody String dataJson);
}