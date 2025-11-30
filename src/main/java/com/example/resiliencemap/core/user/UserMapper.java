package com.example.resiliencemap.core.user;

import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.user.model.UserAdminResponse;
import com.example.resiliencemap.core.user.model.UserLightweightResponse;
import com.example.resiliencemap.core.user.model.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(User user) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setId(user.getId());
        userProfileResponse.setUsername(user.getUsername());
        userProfileResponse.setFirstName(user.getFirstName());
        userProfileResponse.setLastName(user.getLastName());
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setPhoneNumber(user.getPhoneNumber());
        userProfileResponse.setHideNumberInProfile(user.getHideNumberInProfile());
        userProfileResponse.setRole(user.getRole());
        return userProfileResponse;
    }

    public UserAdminResponse toUserAdminResponse(User user) {
        UserAdminResponse userAdminResponse = new UserAdminResponse();
        userAdminResponse.setId(user.getId());
        userAdminResponse.setEmail(user.getEmail());
        userAdminResponse.setPhoneNumber(user.getPhoneNumber());
        userAdminResponse.setUsername(user.getUsername());
        userAdminResponse.setFirstName(user.getFirstName());
        userAdminResponse.setLastName(user.getLastName());
        userAdminResponse.setRole(user.getRole());
        userAdminResponse.setHideNumberInProfile(user.getHideNumberInProfile());
        userAdminResponse.setCreatedAt(user.getCreatedAt());
        userAdminResponse.setUpdatedAt(user.getUpdatedAt());
        userAdminResponse.setStatus(user.getStatus());
        return userAdminResponse;
    }

    public UserLightweightResponse toLightweightResponse(User user) {
        UserLightweightResponse userLightweightResponse = new UserLightweightResponse();
        userLightweightResponse.setId(user.getId());
        userLightweightResponse.setPhoneNumber(user.getPhoneNumber());
        userLightweightResponse.setUsername(user.getUsername());
        userLightweightResponse.setFirstName(user.getFirstName());
        userLightweightResponse.setLastName(user.getLastName());
        userLightweightResponse.setRole(user.getRole());
        return userLightweightResponse;
    }



}
