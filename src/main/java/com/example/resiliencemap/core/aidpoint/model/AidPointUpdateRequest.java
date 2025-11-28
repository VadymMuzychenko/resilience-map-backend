package com.example.resiliencemap.core.aidpoint.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AidPointUpdateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocationCoordinates location;

    private Boolean showPoint;

    private String address;

    private Long locationTypeId;

    private Boolean addUserDataToContacts;

    private List<Long> serviceIds;

    private List<Long> draftPhotoIds;
}
