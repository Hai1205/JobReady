package com.example.cvservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = { "com.example.authservice", "com.example.rabbitmq" })
public class CVServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CVServiceApplication.class, args);
    }
}