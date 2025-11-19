package com.example.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.configs.SecurityConfig;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
@Import(SecurityConfig.class)
public class AiserviceApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(AiserviceApplication.class, args);
    }
}