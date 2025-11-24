package com.example.resiliencemap.core.authtoken;

import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.exception.BadRequestException;
import com.example.resiliencemap.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;
    private final JwtUtil jwtUtil;

    public AuthToken generateToken(User user) {
        if (user.getStatus().equals(User.StatusType.ACTIVE)) {
            AuthToken authToken = new AuthToken();
            authToken.setUser(user);
            authToken.setRevoked(false);
            authToken.setToken(jwtUtil.generateToken(user.getUsername()));
            return authTokenRepository.save(authToken);
        } else {
            throw new BadRequestException("user status does not match");
        }
    }

    public void revokeToken(User user, String token) {
        AuthToken authToken = authTokenRepository.findByUserAndToken(user, token);
        authToken.setRevoked(true);
        authTokenRepository.save(authToken);
    }

    public boolean checkIsTokenValid(String token, String username) {
        if (jwtUtil.validateToken(token, username)) {
            Optional<AuthToken> optional = authTokenRepository.findByTokenAndRevokedFalseAndUser_Username(token, username);
            return optional.isPresent();
        }
        return false;
    }
}
