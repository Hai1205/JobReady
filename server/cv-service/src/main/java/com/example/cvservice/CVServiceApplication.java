package com.example.cvservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import com.example.securitycommon.configs.SecurityConfig;
// import com.example.cvservice.configs.FeignConfig;

@SpringBootApplication
@Import({SecurityConfig.class
    // , FeignConfig.class
})
@EnableFeignClients
public class CVServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CVServiceApplication.class, args);
    }
}