package com.example.statsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.config.SecurityCommonAutoConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
// @ComponentScan(basePackages = { "com.example.statsservice", "com.example.securitycommon" })
@Import(SecurityCommonAutoConfiguration.class)
public class StatsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatsServiceApplication.class, args);
    }
}
