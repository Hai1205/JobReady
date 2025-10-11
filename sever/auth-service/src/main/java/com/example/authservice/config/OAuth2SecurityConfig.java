package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Cấu hình CORS và CSRF
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())

                // Cấu hình authorization
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/**").permitAll() // Auth endpoints
                        .requestMatchers("/oauth2/**").permitAll() // OAuth2 endpoints
                        .requestMatchers("/actuator/**").permitAll() // Actuator endpoints
                        .requestMatchers("/error").permitAll() // Error page
                        .anyRequest().authenticated())

                // Cấu hình OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/oauth2/callback/*"))
                        // Default success URL sẽ được override bởi custom handlers
                        .defaultSuccessUrl("/oauth2/callback/success", true)
                        .failureUrl("/oauth2/error")
                // Custom success handler có thể được thêm ở đây nếu cần
                );

        return http.build();
    }
}