package com.example.authservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authservice.securitys.JsonAccessDeniedHandler;
import com.example.authservice.securitys.JsonAuthenticationEntryPoint;
import com.example.authservice.securitys.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JsonAuthenticationEntryPoint authenticationEntryPoint;
        private final JsonAccessDeniedHandler accessDeniedHandler;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        JsonAuthenticationEntryPoint authenticationEntryPoint,
                        JsonAccessDeniedHandler accessDeniedHandler) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authenticationEntryPoint = authenticationEntryPoint;
                this.accessDeniedHandler = accessDeniedHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // Cấu hình CORS và CSRF
                                .cors(cors -> cors.disable())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Cấu hình authorization
                                .authorizeHttpRequests(authz -> authz
                                                // Public endpoints - không cần authentication
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register")
                                                .permitAll()
                                                .requestMatchers("/api/v1/auth/send-otp/**",
                                                                "/api/v1/auth/verify-otp/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/auth/forgot-password/**").permitAll()
                                                .requestMatchers("/api/v1/auth/refresh-token").permitAll() // Cho phép
                                                                                                           // refresh
                                                                                                           // token
                                                                                                           // không cần
                                                                                                           // access
                                                                                                           // token
                                                .requestMatchers("/oauth2/**").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/error").permitAll()

                                                // Protected endpoints - yêu cầu authentication
                                                .requestMatchers("/api/v1/auth/logout").authenticated()
                                                .requestMatchers("/api/v1/auth/change-password/**").authenticated()
                                                .requestMatchers("/api/v1/auth/reset-password/**").authenticated()
                                                .requestMatchers("/api/v1/auth/health*").authenticated()

                                                .anyRequest().authenticated())

                                // Exception handling
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                // Cấu hình OAuth2 Login
                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/oauth2/authorization"))
                                                .redirectionEndpoint(redirection -> redirection
                                                                .baseUri("/oauth2/callback/*"))
                                                .defaultSuccessUrl("/oauth2/callback/success", true)
                                                .failureUrl("/oauth2/error"))

                                // Thêm JWT filter
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}