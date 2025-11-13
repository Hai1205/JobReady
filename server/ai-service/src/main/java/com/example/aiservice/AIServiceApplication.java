package com.example.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;
import com.example.securitycommon.config.SecurityCommonAutoConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
@Import(SecurityCommonAutoConfiguration.class)
public class AiserviceApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(AiserviceApplication.class, args);
    }
}