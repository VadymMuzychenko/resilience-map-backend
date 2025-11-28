package com.example.resiliencemap.core.user;


import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.user.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserResponse toUserResponse(User user, User actor) {
        UserResponse userResponse = new UserResponse();
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

}
