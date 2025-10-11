package com.example.cvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cvservice.dto.CVDto;
import com.example.cvservice.service.CVService;

import java.util.List;

@RestController
@RequestMapping("/cvs")
public class CVController {

    @Autowired
    private CVService userService;

    @PostMapping
    public ResponseEntity<CVDto> createCV(@RequestBody CVDto cvDto) {
        try {
            CVDto createdCV = userService.createCV(cvDto);
            return new ResponseEntity<>(createdCV, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CVDto>> getAllCVs() {
        List<CVDto> cvs = userService.getAllCVs();
        return ResponseEntity.ok(cvs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CVDto> getCVById(@PathVariable("id") Long id) {
        try {
            CVDto cv = userService.getCVById(id);
            return ResponseEntity.ok(cv);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<CVDto> getCVByTitle(@PathVariable("title") String title) {
        try {
            CVDto cv = userService.getCVByTitle(title);
            return ResponseEntity.ok(cv);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CVDto> updateCV(@PathVariable("id") Long id, @RequestBody CVDto cvDto) {
        try {
            CVDto updatedCV = userService.updateCV(id, cvDto);
            return ResponseEntity.ok(updatedCV);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCV(@PathVariable("id") Long id) {
        try {
            userService.deleteCV(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CV Service is running");
    }
}