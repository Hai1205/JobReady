package com.example.authservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.config.SecurityCommonAutoConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@ComponentScan(basePackages = { "com.example.authservice", "com.example.rabbitmq" })
@Import(SecurityCommonAutoConfiguration.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}