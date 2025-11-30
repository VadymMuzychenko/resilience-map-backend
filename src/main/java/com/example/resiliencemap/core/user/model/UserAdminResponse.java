package com.example.resiliencemap.core.user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class UserAdminResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private String username;
    private String firstName;
    private String lastName;
    private User.UserRole role;
    private Boolean hideNumberInProfile;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private User.StatusType status;
}
