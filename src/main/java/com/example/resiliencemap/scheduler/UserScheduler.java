package com.example.resiliencemap.scheduler;

import com.example.resiliencemap.core.user.UserCleanupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private static final Logger log = LoggerFactory.getLogger(UserScheduler.class);
    private final UserCleanupService userCleanupService;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void deleteUnverifiedUsersEveryFiveMinutes() {
        Duration expirationAge = Duration.ofMinutes(30);
        OffsetDateTime expirationTime = OffsetDateTime.now().minus(expirationAge);
        int deleted = userCleanupService.deleteExpiredUsers(expirationTime);
        if (deleted > 0) {
            log.info("Deleted {} unverified users", deleted);
        }
    }
}
