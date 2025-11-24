package com.example.resiliencemap.core.servicetype.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceTypeCreateRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String smsCode;
    @NotBlank
    private String name;
    private String description;
}
