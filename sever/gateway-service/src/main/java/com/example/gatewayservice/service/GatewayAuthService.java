package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.AuthRequest;
import com.example.gatewayservice.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GatewayAuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<AuthResponse> authenticateUser(AuthRequest authRequest) {
        // First, validate user credentials with User Service
        return validateUserCredentials(authRequest)
                .flatMap(isValid -> {
                    if (isValid) {
                        // If valid, get token from Auth Service
                        return getTokenFromAuthService(authRequest);
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });
    }

    private Mono<Boolean> validateUserCredentials(AuthRequest authRequest) {
        return webClientBuilder.build()
                .post()
                .uri("http://user-service/users/authenticate")
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    private Mono<AuthResponse> getTokenFromAuthService(AuthRequest authRequest) {
        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/auth/login")
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }

    public Mono<Boolean> validateToken(String token, String username) {
        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/auth/validate?token={token}&username={username}", token, username)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }
}