package com.example.cvservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@ComponentScan(basePackages = { "com.example.cvservice", "com.example.rabbitmq" })
public class CVServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CVServiceApplication.class, args);
    }
}