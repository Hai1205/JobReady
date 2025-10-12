package com.example.userservice.initializer;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RootUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RootUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ROOT_EMAIL}")
    private String rootEmail;

    @Value("${ROOT_PASSWORD}")
    private String rootPassword;

    @Value("${ROOT_FULLNAME}")
    private String rootFullname;

    @Value("${ROOT_USERNAME}")
    private String rootUsername;

    public RootUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking if root user exists...");

        // Check if root user exists by email
        Optional<User> existingUser = userRepository.findByEmail(rootEmail);

        if (existingUser.isEmpty()) {
            log.info("Root user not found. Creating root user: {}", rootEmail);

            // Create root user
            User rootUser = new User();
            rootUser.setUsername(rootUsername);
            rootUser.setEmail(rootEmail);
            rootUser.setPassword(passwordEncoder.encode(rootPassword));
            rootUser.setFullname(rootFullname);
            rootUser.setRole(User.UserRole.ADMIN);
            rootUser.setStatus(User.UserStatus.ACTIVE);

            userRepository.save(rootUser);
            log.info("Root user created successfully");
        } else {
            log.info("Root user already exists. Skipping creation.");
        }
    }
}