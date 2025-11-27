package com.example.resiliencemap.security.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationCodeRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String destination;
}
