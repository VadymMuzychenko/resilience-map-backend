package com.example.resiliencemap.core.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangeStatusRequest {
    @NotNull
    private User.StatusType newStatus;
}
