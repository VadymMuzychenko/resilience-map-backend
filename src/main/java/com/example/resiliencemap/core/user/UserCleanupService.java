package com.example.resiliencemap.core.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    @Transactional
    public int deleteExpiredUsers(OffsetDateTime expirationTime) {
        return userRepository.deleteUnverifiedUsers(expirationTime);
    }
}
