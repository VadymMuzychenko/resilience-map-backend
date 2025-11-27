package com.example.resiliencemap.security;

import com.example.resiliencemap.core.authtoken.AuthToken;
import com.example.resiliencemap.core.authtoken.AuthTokenService;
import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.verification.VerificationCodeService;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.functional.exception.BadRequestException;
import com.example.resiliencemap.functional.exception.ConflictException;
import com.example.resiliencemap.functional.exception.UnauthorizedException;
import com.example.resiliencemap.functional.utils.ValidationUtil;
import com.example.resiliencemap.security.model.ConfirmRequest;
import com.example.resiliencemap.security.model.LoginRequest;
import com.example.resiliencemap.security.model.RegisterRequest;
import com.example.resiliencemap.security.model.TokenResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationCodeService verificationCodeService;
    private final AuthTokenService authTokenService;


    @Transactional
    public VerificationCodeSendStatusResponse registerUser(RegisterRequest registerRequest) {

        boolean isEmailValid = ValidationUtil.isEmailValid(registerRequest.getEmail());
        boolean isPhoneNumberValid = ValidationUtil.isPhoneNumberValid(registerRequest.getPhoneNumber());

        if (registerRequest.getEmail() != null && !isEmailValid) {
            throw new BadRequestException("Email is invalid");
        }
        if (registerRequest.getPhoneNumber() != null && !isPhoneNumberValid) {
            throw new BadRequestException("Phone number is invalid");
        }
        if (!isEmailValid && !isPhoneNumberValid) {
            throw new BadRequestException("Email or phone number is required");
        }

        if (userRepository.existsByUsername((registerRequest.getUsername()))) {
            throw new ConflictException("A user with that name already exists: " + registerRequest.getUsername());
        }
        if (isEmailValid) {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new ConflictException("A user with that email already exists: " + registerRequest.getEmail());
            }
        }
        if (isPhoneNumberValid) {
            if (registerRequest.getPhoneNumber().startsWith("+")) {
                registerRequest.setPhoneNumber(registerRequest.getPhoneNumber().substring(1));
            }
            if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
                throw new ConflictException("A user with that phone number already exists: " + registerRequest.getPhoneNumber());
            }
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(User.StatusType.PENDING);
        user.setCreatedAt(OffsetDateTime.now());
        User savedUser = userRepository.save(user);

        VerificationCodeSendStatusResponse response = new VerificationCodeSendStatusResponse();
        response.setStatus("FAIL");
        response.setMessage("sending a verification code to email is not implemented");
        if (isPhoneNumberValid) {

            response = verificationCodeService.sendVerificationCodeToPhone(savedUser, registerRequest.getPhoneNumber());
            // TODO: if not sent
        } else if (isEmailValid) {

            response = verificationCodeService.sendVerificationCodeToEmail(savedUser, registerRequest.getEmail());
        }
        return response;
    }

    @Transactional
    public TokenResponse confirmContactMethod(ConfirmRequest confirmRequest) {
        boolean isEmailValid = ValidationUtil.isEmailValid(confirmRequest.getDestination());
        boolean isPhoneNumberValid = ValidationUtil.isPhoneNumberValid(confirmRequest.getDestination());
        if (!isEmailValid && !isPhoneNumberValid) {
            throw new BadRequestException("Destination is invalid");
        }
        if (isPhoneNumberValid) {
            if (confirmRequest.getDestination().startsWith("+")) {
                confirmRequest.setDestination(confirmRequest.getDestination().substring(1));
            }
            if (userRepository.existsByPhoneNumber(confirmRequest.getDestination())) {
                throw new ConflictException("A user with that phone number already exists: " + confirmRequest.getDestination());
            }
        }
        if (isEmailValid) {
            if (userRepository.existsByEmail(confirmRequest.getDestination())) {
                throw new ConflictException("A user with that email already exists: " + confirmRequest.getDestination());
            }
        }

        User user = verificationCodeService.confirmContactVerification(confirmRequest.getCode(), confirmRequest.getDestination());
        AuthToken token = authTokenService.generateToken(user);
        return new TokenResponse(token.getToken());
    }

    public TokenResponse loginUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid login or password");
        }
        User user = userRepository.getUserByUsername((loginRequest.getUsername()));
        AuthToken token = authTokenService.generateToken(user);
        return new TokenResponse(token.getToken());
    }

    public void logoutUser(User user, String token) {
        authTokenService.revokeToken(user, token);
    }
}
