package com.example.resiliencemap.core.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangeRoleRequest {
    @NotNull
    public User.UserRole newRole;
}
