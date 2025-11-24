package com.example.resiliencemap.core.aidpoint.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidPointCreateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocationCoordinates location;

    private Boolean showPoint;

    private String address;

    private Long locationTypeId;

    private Boolean addUserDataToContacts;

}
