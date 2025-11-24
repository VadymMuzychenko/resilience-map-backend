package com.example.resiliencemap.core.aidpoint.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationCoordinates {
    @NotNull(message = "latitude is required")
    private double latitude;
    @NotNull(message = "longitude is required")
    private double longitude;

    public LocationCoordinates(Point point) {
        LocationCoordinates locationCoordinates = new LocationCoordinates();
        latitude = point.getY();
        longitude = point.getX();
    }
}
