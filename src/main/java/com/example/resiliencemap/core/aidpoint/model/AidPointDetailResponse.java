package com.example.resiliencemap.core.aidpoint.model;

import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.locationtype.model.LocationTypeResponse;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeResponse;
import com.example.resiliencemap.core.user.model.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AidPointDetailResponse {

    private Long id;
    private String name;
    private String description;
    private String status; // TODO: status

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private LocationCoordinates location;
    private String address;

    private LocationTypeResponse locationType;
    private UserResponse createdBy;

    private List<AidPointContactResponse> contacts;
    private List<ServiceTypeResponse> services;

}
