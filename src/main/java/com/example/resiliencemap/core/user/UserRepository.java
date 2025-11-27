package com.example.resiliencemap.core.user;

import com.example.resiliencemap.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User getUserByUsername(String username);

    boolean existsByEmail(String email);

    long countByRole(User.UserRole role);

    User getUserByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select u from User u where u.username = ?1 and u.status = 'PENDING'")
    Optional<User> findInactiveUser(String username);
}
