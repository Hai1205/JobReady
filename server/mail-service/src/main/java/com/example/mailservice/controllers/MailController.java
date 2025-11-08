package com.example.mailservice.controllers;

import com.example.mailservice.dtos.responses.*;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mails")
public class MailController {
    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "Mail Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}