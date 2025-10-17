package com.example.userservice.initializer;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RootUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RootUserInitializer.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${ROOT_EMAIL}")
    private String rootEmail;

    @Value("${ROOT_PASSWORD}")
    private String rootPassword;

    @Value("${ROOT_FULLNAME}")
    private String rootFullname;

    @Value("${ROOT_USERNAME}")
    private String rootUsername;

    @Value("${ROOT_ROLE}")
    private String rootRole;

    @Value("${ROOT_STATUS}")
    private String rootStatus;

    public RootUserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking if root user exists...");

        // Check if root user exists by email
        UserDto existingUser = userService.handleFindByEmail(rootEmail);

        if (existingUser == null) {
            log.info("Root user not found. Creating root user: {}", rootEmail);

            userService.handleCreateUser(rootUsername, rootEmail, rootPassword, rootFullname, rootRole, rootStatus, null);

            log.info("Root user created successfully");
        } else {
            log.info("Root user already exists. Skipping creation.");
        }
    }
}