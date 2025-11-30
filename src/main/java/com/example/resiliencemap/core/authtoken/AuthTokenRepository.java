package com.example.resiliencemap.core.authtoken;

import com.example.resiliencemap.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    @Query("select a from AuthToken a where a.token = ?1 and a.revoked = false and a.user.username = ?2")
    Optional<AuthToken> findByTokenAndRevokedFalseAndUser_Username(String token, String username);

    AuthToken findByUserAndToken(User user, String token);

    @Query("select a from AuthToken a where a.user.id = ?1 and a.token = ?2 and a.revoked = false")
    AuthToken findByUser_IdAndTokenAndRevoked(Long id, String token);


}
