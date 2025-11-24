package com.example.resiliencemap.core.contact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidPointContactCreateRequest {
    @NotNull
    private Long aidPointId;
    @NotBlank
    private String fullName;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String role;
    @JsonProperty("isHide")
    private Boolean isHide;
}
