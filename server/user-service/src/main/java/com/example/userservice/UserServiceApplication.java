package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.configs.SecurityConfig;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.userservice", "com.example.cloudinarycommon"})
@Import(SecurityConfig.class)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}