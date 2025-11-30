package com.example.resiliencemap.core.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLightweightResponse {

    private Long id;
    private String phoneNumber;
    private String username;
    private String firstName;
    private String lastName;
    private User.UserRole role;

}
