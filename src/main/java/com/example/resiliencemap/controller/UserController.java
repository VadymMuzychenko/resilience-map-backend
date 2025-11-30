package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.user.UserService;
import com.example.resiliencemap.core.user.model.*;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.functional.model.PagingList;
import com.example.resiliencemap.security.model.ChangePasswordRequest;
import com.example.resiliencemap.security.model.ConfirmRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/{userId}")
    public UserProfileResponse editUser(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody UserEditRequest request,
                                        @AuthenticationPrincipal User user) {
        return userService.editUser(userId, request, user);
    }

    @GetMapping("/user/all")
    public PagingList<UserLightweightResponse> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "search-query", required = false) String searchQuery,
            @AuthenticationPrincipal User user) {
        return userService.getAllUsers(searchQuery, pageNumber, pageSize, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user/{userId}/change-role")
    public UserAdminResponse changeRole(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody UserChangeRoleRequest request) {
        return userService.changeUserRole(userId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user/{userId}/change-status")
    public UserAdminResponse changeStatus(@PathVariable("userId") Long userId,
                                          @Valid @RequestBody UserChangeStatusRequest request) {
        return userService.changeUserStatus(userId, request);
    }

    @PostMapping("/user/me/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request,
                               @AuthenticationPrincipal User user,
                               @RequestHeader(name = "Authorization") String token) {
        userService.changePassword(request, user, token);
    }

    @PostMapping("/user/me/change-username")
    public void changeUsername(@Valid @RequestBody UserChangeUsernameRequest request,
                               @AuthenticationPrincipal User user,
                               @RequestHeader(name = "Authorization") String token) {
        userService.changeUsername(request, user, token);
    }

    @PostMapping("/user/me/change-phone-number")
    public VerificationCodeSendStatusResponse changePhoneNumber(@Valid @RequestBody UserChangePhoneNumberRequest request,
                                                             @AuthenticationPrincipal User user) {
        return userService.changePhoneNumber(request, user);
    }

    @PostMapping("/user/me/change-email")
    public VerificationCodeSendStatusResponse changeEmail(@Valid @RequestBody UserChangeEmailRequest request,
                                                             @AuthenticationPrincipal User user) {
        return userService.changeEmail(request, user);
    }

    @PostMapping("/user/verification-code/confirm")
    public UserConfirmContactMethodResponse confirmContactMethod(@Valid @RequestBody ConfirmRequest confirmRequest) {
        return userService.confirmContactMethod(confirmRequest);
    }
}
