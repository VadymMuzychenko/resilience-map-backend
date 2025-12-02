package com.example.resiliencemap.core.servicetype;

import com.example.resiliencemap.core.servicetype.model.ServiceType;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeCreateRequest;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeResponse;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeUpdateRequest;
import com.example.resiliencemap.functional.exception.ConflictException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeResponse createServiceType(ServiceTypeCreateRequest serviceTypeCreateRequest) {
        if (serviceTypeRepository.existsByCode(serviceTypeCreateRequest.getCode())) {
            throw new ConflictException("ServiceType with this code already exists");
        }
        if (serviceTypeRepository.existsBySmsCode(serviceTypeCreateRequest.getSmsCode())) {
            throw new ConflictException("ServiceType with this sms code already exists");
        }
        ServiceType serviceType = ServiceTypeMapper.toServiceType(serviceTypeCreateRequest);
        return ServiceTypeMapper.toServiceTypeResponse(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeResponse getServiceType(Long id) {
        return ServiceTypeMapper.toServiceTypeResponse(getServiceTypeFromRepository(id));
    }

    public ServiceType getServiceTypeFromRepository(Long id) {
        Optional<ServiceType> optional = serviceTypeRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("ServiceType not found");
        }
    }

    public List<ServiceTypeResponse> getServiceTypes() {
        List<ServiceType> locationTypeList = serviceTypeRepository.findAll();
        return locationTypeList.stream()
                .map(ServiceTypeMapper::toServiceTypeResponse).toList();
    }

    public ServiceTypeResponse updateServiceType(Long serviceTypeId, ServiceTypeUpdateRequest request) {
        if (serviceTypeRepository.existsByCode(request.getCode())) {
            throw new ConflictException("ServiceType with this code already exists");
        }
        if (serviceTypeRepository.existsBySmsCode(request.getSmsCode())) {
            throw new ConflictException("ServiceType with this sms code already exists");
        }
        ServiceType serviceType = getServiceTypeFromRepository(serviceTypeId);
        serviceType.setCode(request.getCode());
        serviceType.setName(request.getName());
        serviceType.setDescription(request.getDescription());
        serviceType.setSmsCode(request.getSmsCode());
        return ServiceTypeMapper.toServiceTypeResponse(serviceTypeRepository.save(serviceType));
    }

    public void deleteServiceType(Long id) {
        ServiceType serviceType = getServiceTypeFromRepository(id);
        serviceTypeRepository.delete(serviceType);
    }
}
