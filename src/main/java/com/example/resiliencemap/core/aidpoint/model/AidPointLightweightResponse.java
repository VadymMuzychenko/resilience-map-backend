package com.example.resiliencemap.core.aidpoint.model;

import com.example.resiliencemap.core.locationtype.model.LocationTypeResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidPointLightweightResponse {

    private Long id;
    private String name;
    private String description;
//    private String staus; // TODO: status


    private LocationCoordinates location;
    private String address;

    private LocationTypeResponse locationType;
}
