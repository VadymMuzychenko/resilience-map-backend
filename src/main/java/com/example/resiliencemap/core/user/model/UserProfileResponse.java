package com.example.resiliencemap.core.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private String email;
    private String phoneNumber;
    private String username;
    private String firstName;
    private String lastName;
    private User.UserRole role;
    private Boolean hideNumberInProfile = false;
}
