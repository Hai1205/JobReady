package com.example.cvservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.example.securitycommon.configs.SecurityConfig;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.cvservice", "com.example.cloudinarycommon"})
@Import({ SecurityConfig.class })
@EnableFeignClients
@EnableAsync
public class CVServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CVServiceApplication.class, args);
    }
}