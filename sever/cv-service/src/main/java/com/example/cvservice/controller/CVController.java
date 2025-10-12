package com.example.cvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cvservice.dto.CVDto;
import com.example.cvservice.dto.Response;
import com.example.cvservice.service.CVService;

@RestController
@RequestMapping("/cvs")
public class CVController {

    @Autowired
    private CVService userService;

    @PostMapping
    public ResponseEntity<Response> createCV(@RequestBody CVDto cvDto) {
        Response response = userService.createCV(cvDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response> getAllCVs() {
        Response response = userService.getAllCVs();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCVById(@PathVariable("id") Long id) {
        Response response = userService.getCVById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Response> getCVByTitle(@PathVariable("title") String title) {
        Response response = userService.getCVByTitle(title);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCV(@PathVariable("id") Long id, @RequestBody CVDto cvDto) {
        Response response = userService.updateCV(id, cvDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCV(@PathVariable("id") Long id) {
        Response response = userService.deleteCV(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response(200, "CV Service is running");
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}