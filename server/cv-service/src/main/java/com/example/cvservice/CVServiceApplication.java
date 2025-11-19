package com.example.cvservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.configs.SecurityConfig;

@SpringBootApplication
@EnableDiscoveryClient
// @ComponentScan(basePackages = { "com.example.cvservice" })
@Import(SecurityConfig.class)
public class CVServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CVServiceApplication.class, args);
    }
}