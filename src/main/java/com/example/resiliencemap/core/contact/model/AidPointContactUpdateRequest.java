package com.example.resiliencemap.core.contact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidPointContactUpdateRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String role;
    @JsonProperty("isHide")
    private Boolean isHide;
}
