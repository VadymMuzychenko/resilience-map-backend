package com.example.resiliencemap.core.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePhoneNumberRequest {
    @NotBlank
    private String newPhoneNumber;
}
