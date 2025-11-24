package com.example.resiliencemap.core.locationtype;

import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.locationtype.model.LocationTypeCreateRequest;
import com.example.resiliencemap.core.locationtype.model.LocationTypeResponse;

public class LocationTypeMapper {

    public static LocationType toLocationType(LocationTypeCreateRequest locationTypeCreateRequest){
        LocationType locationType = new LocationType();
        locationType.setName(locationTypeCreateRequest.getName());
        locationType.setDescription(locationTypeCreateRequest.getDescription());
        locationType.setCode(locationTypeCreateRequest.getCode());
        locationType.setSmsCode(locationTypeCreateRequest.getSmsCode());
        return locationType;
    }

    public static LocationTypeResponse toLocationTypeResponse(LocationType locationType){
        LocationTypeResponse locationTypeResponse = new LocationTypeResponse();
        locationTypeResponse.setId(locationType.getId());
        locationTypeResponse.setName(locationType.getName());
        locationTypeResponse.setDescription(locationType.getDescription());
        locationTypeResponse.setCode(locationType.getCode());
        locationTypeResponse.setSmsCode(locationType.getSmsCode());
        return locationTypeResponse;
    }
}
