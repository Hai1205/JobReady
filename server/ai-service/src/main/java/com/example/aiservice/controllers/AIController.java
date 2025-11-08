package com.example.aiservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aiservice.dtos.response.Response;


@RestController
@RequestMapping("/ai")
public class AIController {

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("AI Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}