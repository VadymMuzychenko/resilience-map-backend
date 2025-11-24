package com.example.resiliencemap.functional;

import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class FirstStartLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(FirstStartLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.countByRole(User.UserRole.ADMIN) == 0) {
            String baseUsername = "admin";
            String candidateUsername = baseUsername;
            int counter = 1;

            while (userRepository.getUserByUsername(candidateUsername) != null) {
                candidateUsername = baseUsername + counter;
                counter++;
            }

            User admin = new User();
            admin.setUsername(candidateUsername);
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setEmail("admin@email.com");
            admin.setRole(User.UserRole.ADMIN);
            admin.setStatus(User.StatusType.ACTIVE);
            admin.setCreatedAt(OffsetDateTime.now());
            userRepository.save(admin);
            logger.warn("Administrator not found. New administrator created: " + candidateUsername);
        }
    }
}
