package com.example.resiliencemap.core.user;

import com.example.resiliencemap.core.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
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

    @Query("""
            select u from User u
            where  u.status = 'ACTIVE' and
                    u.username like CONCAT('%', ?1, '%')
                    or u.firstName like CONCAT('%', ?1, '%')
                    or u.lastName like CONCAT('%', ?1, '%')""")
    Page<User> findUsersForUser(String searchQuery, Pageable pageable);

    @Query("""
            select u from User u
            where  u.status = 'ACTIVE'""")
    Page<User> findUsersForUser(Pageable pageable);

    @Query("""
            select u from User u
            where  u.username like CONCAT('%', ?1, '%')
                    or u.firstName like CONCAT('%', ?1, '%')
                    or u.lastName like CONCAT('%', ?1, '%')""")
    Page<User> findUsersForAdmin(String searchQuery, Pageable pageable);

    @Query("select u from User u")
    Page<User> findUsersForAdmin(Pageable pageable);

    @Modifying
    @Query("DELETE FROM User u WHERE u.status = 'PENDING' AND u.createdAt < :expirationTime")
    int deleteUnverifiedUsers(@Param("expirationTime") OffsetDateTime expirationTime);
}
