package com.example.resiliencemap.core.user;


import com.example.resiliencemap.core.user.model.*;
import com.example.resiliencemap.core.verification.VerificationCodeService;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.functional.exception.*;
import com.example.resiliencemap.functional.model.PagingList;
import com.example.resiliencemap.functional.utils.ValidationUtil;
import com.example.resiliencemap.security.AuthService;
import com.example.resiliencemap.security.model.ChangePasswordRequest;
import com.example.resiliencemap.security.model.ConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    public UserLightweightResponse toUserResponse(User user, User actor) {
        UserLightweightResponse userResponse = new UserLightweightResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setRole(user.getRole());
        if (!user.getHideNumberInProfile() || User.UserRole.ADMIN.equals(actor.getRole())) {
            userResponse.setPhoneNumber(user.getPhoneNumber());
        }
        return userResponse;
    }

    public UserProfileResponse editUser(Long userId, UserEditRequest request, User actor) {
        User user = getUserById(userId);
        if (!(User.UserRole.ADMIN.equals(actor.getRole()) || user.getId().equals(actor.getId()))) {
            throw new ForbiddenException("Access denied");
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setHideNumberInProfile(request.getHideNumberInProfile());
        return userMapper.toUserProfileResponse(userRepository.save(user));
    }

    public PagingList<UserLightweightResponse> getAllUsers(String searchQuery, int pageNumber, int pageSize, User user) {
        Page<User> usersPage;
        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        if (User.UserRole.ADMIN.equals(user.getRole())) {
            if (searchQuery != null && !searchQuery.isBlank()) {
                usersPage = userRepository.findUsersForAdmin(searchQuery, pageable);
            } else {
                usersPage = userRepository.findUsersForAdmin(pageable);
            }
        } else {
            if (searchQuery != null && !searchQuery.isBlank()) {
                usersPage = userRepository.findUsersForUser(searchQuery, pageable);
            } else {
                usersPage = userRepository.findUsersForUser(pageable);
            }
        }
        List<UserLightweightResponse> users = usersPage.stream().map(userMapper::toLightweightResponse).toList();
        return new PagingList<>(pageNumber, usersPage.getTotalPages(), usersPage.getTotalElements(), users);
    }

    public UserAdminResponse changeUserRole(Long userId, UserChangeRoleRequest request) {
        User user = getUserById(userId);
        user.setRole(request.getNewRole());
        return userMapper.toUserAdminResponse(userRepository.save(user));
    }

    public UserAdminResponse changeUserStatus(Long userId, UserChangeStatusRequest request) {
        User user = getUserById(userId);
        user.setStatus(request.getNewStatus());
        return userMapper.toUserAdminResponse(userRepository.save(user));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void changeUsername(UserChangeUsernameRequest request, User actor, String token) {
        if (userRepository.existsByUsername(request.getNewUsername())) {
            throw new ConflictException("A user with that username already exists: " + request.getNewUsername());
        }
        actor.setUsername(request.getNewUsername());
        User savedUser = userRepository.save(actor);
        authService.logoutUser(savedUser, token);
    }

    public void changePassword(ChangePasswordRequest request, User actor, String token) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    actor.getUsername(), request.getOldPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid old password");
        }

        actor.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(actor);
        authService.logoutUser(actor, token);
    }

    public VerificationCodeSendStatusResponse changePhoneNumber(UserChangePhoneNumberRequest request, User actor) {
        if (!ValidationUtil.isPhoneNumberValid(request.getNewPhoneNumber())) {
            throw new BadRequestException("Invalid phone number");
        }
        if (request.getNewPhoneNumber().startsWith("+")) {
            request.setNewPhoneNumber(request.getNewPhoneNumber().substring(1));
        }
        if (userRepository.existsByPhoneNumber(request.getNewPhoneNumber())) {
            throw new ConflictException("A user with that phone number already exists: " + request.getNewPhoneNumber());
        }
        return verificationCodeService.sendVerificationCodeToPhone(actor, request.getNewPhoneNumber());
    }

    public VerificationCodeSendStatusResponse changeEmail(UserChangeEmailRequest request, User actor) {
        if (!ValidationUtil.isEmailValid(request.getNewEmail())) {
            throw new BadRequestException("Invalid phone number");
        }
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new ConflictException("A user with that phone number already exists: " + request.getNewEmail());
        }
        return verificationCodeService.sendVerificationCodeToEmail(actor, request.getNewEmail());
    }

    public UserConfirmContactMethodResponse confirmContactMethod(ConfirmRequest request) {
        boolean isEmailValid = ValidationUtil.isEmailValid(request.getDestination());
        boolean isPhoneNumberValid = ValidationUtil.isPhoneNumberValid(request.getDestination());
        if (!isEmailValid && !isPhoneNumberValid) {
            throw new BadRequestException("Destination is invalid");
        }
        if (isPhoneNumberValid) {
            if (request.getDestination().startsWith("+")) {
                request.setDestination(request.getDestination().substring(1));
            }
            if (userRepository.existsByPhoneNumber(request.getDestination())) {
                throw new ConflictException("A user with that phone number already exists: " + request.getDestination());

            }
        }
        if (isEmailValid) {
            if (userRepository.existsByPhoneNumber(request.getDestination())) {
                throw new ConflictException("A user with that email already exists: " + request.getDestination());
            }
        }
        verificationCodeService.confirmContactVerification(request.getCode(), request.getDestination());
        return new UserConfirmContactMethodResponse().setMessage("OK");
    }
}
