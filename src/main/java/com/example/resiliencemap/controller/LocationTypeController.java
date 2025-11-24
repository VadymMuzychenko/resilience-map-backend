package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.locationtype.LocationTypeService;
import com.example.resiliencemap.core.locationtype.model.LocationTypeCreateRequest;
import com.example.resiliencemap.core.locationtype.model.LocationTypeResponse;
import com.example.resiliencemap.core.locationtype.model.LocationTypeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location-type")
@RequiredArgsConstructor
public class LocationTypeController {

    private final LocationTypeService locationTypeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public LocationTypeResponse createLocationType(@Valid @RequestBody LocationTypeCreateRequest request) {
        return locationTypeService.createLocationType(request);
    }

    @GetMapping("/{locationTypeId}")
    public LocationTypeResponse getLocationType(@PathVariable("locationTypeId") Long locationTypeId) {
        return locationTypeService.getLocationTypeResponse(locationTypeId);
    }

    @GetMapping("/all")
    public List<LocationTypeResponse> getAllLocationTypes() {
        return locationTypeService.getLocationTypes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{locationTypeId}")
    public LocationTypeResponse updateLocationType(@PathVariable("locationTypeId") Long locationTypeId,
                                                   @RequestBody LocationTypeUpdateRequest request) {
        return locationTypeService.updateLocationType(locationTypeId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{locationTypeId}")
    public void deleteLocationType(@PathVariable("locationTypeId") Long locationTypeId) {
        locationTypeService.deleteLocationType(locationTypeId);
    }
}
