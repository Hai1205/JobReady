package com.example.userservice.controllers;

import com.example.userservice.dtos.response.Response;
import com.example.userservice.services.apis.PlanApi;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/plans")
public class PlanController {

    @Autowired
    private PlanApi planApi;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> createPlan(@RequestPart("data") String dataJson) {
        Response response = planApi.createPlan(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllPlans() {
        Response response = planApi.getAllPlans();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<Response> getPlanById(@PathVariable("planId") UUID planId) {
        Response response = planApi.getPlanById(planId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{planId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> updatePlan(
            @PathVariable("planId") UUID planId,
            @RequestPart("data") String dataJson) {
        Response response = planApi.updatePlan(planId, dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{planId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Response> deletePlan(@PathVariable("planId") UUID planId) {
        Response response = planApi.deletePlan(planId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("Plan Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}