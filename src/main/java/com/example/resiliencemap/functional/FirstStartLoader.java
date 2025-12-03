package com.example.resiliencemap.functional;

import com.example.resiliencemap.core.aidpoint.AidPointRepository;
import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final DataInitializer dataInitializer;
    private final AidPointRepository aidPointRepository;
    @Value("${application.init.load-test-data}")
    private Boolean loadTestData;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.countByRole(User.UserRole.ADMIN) == 0) {
            createAdmin();
        }
        if (loadTestData) {
            try {
                long count = aidPointRepository.count();
                if (count == 0) {
                    logger.info("Starting initial data load...");
                    dataInitializer.loadTestData();
                    logger.info("Test data successfully initialized.");
                } else {
                    logger.info("Test data load skipped. Database already contains {} AidPoints.", count);
                }
            } catch (Exception e) {
                logger.error("Failed to initialize test data", e);
            }
        }
    }

    private void createAdmin() {
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
