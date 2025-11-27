package com.example.authservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.example.rediscommon.configs.RedisConfig;
import com.example.securitycommon.configs.SecurityConfig;
// import com.example.authservice.configs.FeignConfig;

@SpringBootApplication
@EnableRabbit
@ComponentScan(basePackages = { "com.example.authservice", "com.example.rabbitmq", "com.example.rediscommon" })
@Import({ SecurityConfig.class, RedisConfig.class
    // , FeignConfig.class 
})
@EnableFeignClients
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}