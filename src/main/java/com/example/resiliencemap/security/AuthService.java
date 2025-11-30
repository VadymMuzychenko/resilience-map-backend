package com.example.resiliencemap.security;

import com.example.resiliencemap.core.authtoken.AuthToken;
import com.example.resiliencemap.core.authtoken.AuthTokenService;
import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.verification.VerificationCodeService;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.functional.exception.BadRequestException;
import com.example.resiliencemap.functional.exception.ConflictException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import com.example.resiliencemap.functional.exception.UnauthorizedException;
import com.example.resiliencemap.functional.utils.ValidationUtil;
import com.example.resiliencemap.security.model.*;
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
            checkUserExistsByEmail(registerRequest.getEmail());
        }
        if (isPhoneNumberValid) {
            registerRequest.setPhoneNumber(normalizePhone(registerRequest.getPhoneNumber()));
            checkUserExistsByPhoneNumber(registerRequest.getPhoneNumber());
        }
        User user = buildNewUser(registerRequest);
        User savedUser = userRepository.save(user);

        VerificationCodeSendStatusResponse response = new VerificationCodeSendStatusResponse();
        response.setStatus("FAIL");
        if (isPhoneNumberValid) {

            response = verificationCodeService.sendVerificationCodeToPhone(savedUser, registerRequest.getPhoneNumber());
            // TODO: if not sent
        } else if (isEmailValid) {

            response = verificationCodeService.sendVerificationCodeToEmail(savedUser, registerRequest.getEmail());
        }
        return response;
    }

    private User buildNewUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(User.StatusType.PENDING);
        user.setCreatedAt(OffsetDateTime.now());
        return user;
    }

    private String normalizePhone(String phone) {
        if (phone != null && phone.startsWith("+")) {
            return phone.substring(1);
        }
        return phone;
    }

    @Transactional
    public TokenResponse confirmContactMethod(ConfirmRequest confirmRequest) {
        boolean isEmailValid = ValidationUtil.isEmailValid(confirmRequest.getDestination());
        boolean isPhoneNumberValid = ValidationUtil.isPhoneNumberValid(confirmRequest.getDestination());
        if (!isEmailValid && !isPhoneNumberValid) {
            throw new BadRequestException("Destination is invalid");
        }
        if (isPhoneNumberValid) {
            confirmRequest.setDestination(normalizePhone(confirmRequest.getDestination()));
            checkUserExistsByPhoneNumber(confirmRequest.getDestination());
        }
        if (isEmailValid) {
            checkUserExistsByEmail(confirmRequest.getDestination());
        }

        User user = verificationCodeService.confirmContactVerification(confirmRequest.getCode(), confirmRequest.getDestination());
        AuthToken token = authTokenService.generateToken(user);
        return new TokenResponse(token.getToken());
    }

    public VerificationCodeSendStatusResponse resendVerificationCode(ResendVerificationCodeRequest request) {
        boolean isEmailValid = ValidationUtil.isEmailValid(request.getDestination());
        boolean isPhoneNumberValid = ValidationUtil.isPhoneNumberValid(request.getDestination());
        if (!isEmailValid && !isPhoneNumberValid) {
            throw new BadRequestException("Destination is invalid");
        }
        VerificationCodeSendStatusResponse  response = new VerificationCodeSendStatusResponse();
        response.setStatus("FAIL");
        if (isEmailValid) {
            checkUserExistsByEmail(request.getDestination());
            User user = userRepository.findInactiveUser(request.getUsername())
                    .orElseThrow(() -> new NotFoundException("Inactive User not found"));
            response = verificationCodeService.sendVerificationCodeToEmail(user, request.getDestination());
        }
        if (isPhoneNumberValid) {
            request.setDestination(normalizePhone(request.getDestination()));
            checkUserExistsByPhoneNumber(request.getDestination());
            User user = userRepository.findInactiveUser(request.getUsername())
                    .orElseThrow(() -> new NotFoundException("Inactive User not found"));
            response = verificationCodeService.sendVerificationCodeToPhone(user, request.getDestination());
        }
        return response;
    }

    private void checkUserExistsByPhoneNumber(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("A user with that phone number already exists: " + phoneNumber);
        }
    }

    private void checkUserExistsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("A user with that email already exists: " + email);
        }
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
        if (token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        authTokenService.revokeToken(user, token);
    }
}
