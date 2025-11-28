package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.aidpoint.AidPointService;
import com.example.resiliencemap.core.aidpoint.model.AidPointCreateRequest;
import com.example.resiliencemap.core.aidpoint.model.AidPointDetailResponse;
import com.example.resiliencemap.core.aidpoint.model.AidPointLightweightResponse;
import com.example.resiliencemap.core.aidpoint.model.AidPointUpdateRequest;
import com.example.resiliencemap.core.locationtype.model.LocationTypeChangeRequest;
import com.example.resiliencemap.core.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aid-point")
@RequiredArgsConstructor
public class AidPointController {

    private final AidPointService aidPointService;

    @PostMapping
    public AidPointDetailResponse addAidPoint(@Valid @RequestBody AidPointCreateRequest request,
                                              @AuthenticationPrincipal User user) {
        return aidPointService.addAidPoint(request, user);
    }

    @GetMapping("/{aidPointId}")
    public AidPointDetailResponse getAidPoint(@PathVariable("aidPointId") Long aidPointId,
                                              @AuthenticationPrincipal User user) {
        return aidPointService.getAidPoint(aidPointId, user);
    }

    @PostMapping("/{aidPointId}")
    public AidPointDetailResponse updateAidPoint(@PathVariable("aidPointId") Long aidPointId,
                                                 @RequestBody @Valid AidPointUpdateRequest request,
                                                 @AuthenticationPrincipal User user) {
        return aidPointService.updateAidPoint(aidPointId, request, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{aidPointId}/change-location-type")
    public AidPointDetailResponse changeLocationType(@PathVariable("aidPointId") Long aidPointId,
                                                     @RequestBody LocationTypeChangeRequest request,
                                                     @AuthenticationPrincipal User user) {
        return aidPointService.changeLocationType(aidPointId, request, user);
    }

    @GetMapping("/get-all-within-range")
    public ResponseEntity<List<AidPointLightweightResponse>> getAllWithinRange(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("range") Double range) {
        return ResponseEntity.ok(aidPointService.getAidPointsWithinRadius(latitude, longitude, range));
    }

    @PatchMapping("/{aidPointId}/service/{serviceId}")
    public AidPointDetailResponse addServiceType(@PathVariable Long aidPointId,
                                                 @PathVariable Long serviceId,
                                                 @AuthenticationPrincipal User user) {
        return aidPointService.addServiceType(aidPointId, serviceId, user);
    }

    @PatchMapping("/{aidPointId}/service/remove/{serviceId}")
    public AidPointDetailResponse removeServiceType(@PathVariable Long aidPointId,
                                                    @PathVariable Long serviceId,
                                                    @AuthenticationPrincipal User user) {
        return aidPointService.removeServiceType(aidPointId, serviceId, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{aidPointId}")
    public void deleteAidPoint(@PathVariable Long aidPointId) {
        aidPointService.deleteAidPoint(aidPointId);
    }
}
