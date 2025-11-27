package com.example.statsservice.services.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.statsservice.dtos.responses.Response;

@FeignClient(name = "${CV_SERVICE_NAME}", url = "${CV_SERVICE_URL}")
public interface CVFeignClient {

    @GetMapping("/api/v1/cvs/stats/total")
    Response getTotalCVs();

    @GetMapping("/api/v1/cvs/stats/visibility/{visibility}")
    Response getCVsByVisibility(@PathVariable("visibility") boolean isVisibility);

    @GetMapping("/api/v1/cvs/stats/created-range")
    Response getCVsCreatedInRange(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);

    @GetMapping("/api/v1/cvs/recent")
    Response getRecentCVs(@RequestParam("limit") int limit);
}