package com.example.gatewayservice.controller;

import com.example.gatewayservice.dto.AuthRequest;
import com.example.gatewayservice.dto.AuthResponse;
import com.example.gatewayservice.service.GatewayAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway/auth")
public class AuthController {

    @Autowired
    private GatewayAuthService gatewayAuthService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        return gatewayAuthService.authenticateUser(authRequest)
                .map(response -> ResponseEntity.ok(response))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(new AuthResponse(null, "Authentication failed")));
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestParam String token, @RequestParam String username) {
        return gatewayAuthService.validateToken(token, username)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().body(false));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("Gateway Auth Controller is running"));
    }
}