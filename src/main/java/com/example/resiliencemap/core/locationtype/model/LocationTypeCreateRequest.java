package com.example.resiliencemap.core.locationtype.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationTypeCreateRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String smsCode;
    @NotBlank
    private String name;
    private String description;
}
