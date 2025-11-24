package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.servicetype.ServiceTypeService;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeCreateRequest;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeResponse;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-type")
@RequiredArgsConstructor
public class ServiceTypeController {
    private final ServiceTypeService serviceTypeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ServiceTypeResponse createServiceType(@Valid @RequestBody ServiceTypeCreateRequest request) {
        return serviceTypeService.createServiceType(request);
    }

    @GetMapping("/{serviceTypeId}")
    public ServiceTypeResponse getServiceType(@PathVariable("serviceTypeId") Long serviceTypeId) {
        return serviceTypeService.getServiceType(serviceTypeId);
    }

    @GetMapping("/all")
    public List<ServiceTypeResponse> getAllServiceTypes() {
        return serviceTypeService.getServiceTypes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{serviceTypeId}")
    public ServiceTypeResponse updateServiceType(@PathVariable("serviceTypeId") Long serviceTypeId,
                                                  @RequestBody ServiceTypeUpdateRequest request) {
        return serviceTypeService.updateServiceType(serviceTypeId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{serviceTypeId}")
    public void deleteServiceType(@PathVariable("serviceTypeId") Long serviceTypeId) {
        serviceTypeService.deleteServiceType(serviceTypeId);
    }
}
