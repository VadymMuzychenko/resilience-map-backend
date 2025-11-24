package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.security.AuthService;
import com.example.resiliencemap.security.model.ConfirmRequest;
import com.example.resiliencemap.security.model.LoginRequest;
import com.example.resiliencemap.security.model.RegisterRequest;
import com.example.resiliencemap.security.model.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<VerificationCodeSendStatusResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        VerificationCodeSendStatusResponse response = authService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verification-code/confirm")
    public ResponseEntity<TokenResponse> confirmContactMethod(@Valid @RequestBody ConfirmRequest confirmRequest) {
        TokenResponse response = authService.confirmContactMethod(confirmRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // TODO: /verification-code/resend

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse response = authService.loginUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    @RequestHeader(name = "Authorization") String token) {
        authService.logoutUser(user, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
