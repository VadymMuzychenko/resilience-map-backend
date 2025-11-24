package com.example.resiliencemap.core.servicetype;

import com.example.resiliencemap.core.servicetype.model.ServiceType;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeCreateRequest;
import com.example.resiliencemap.core.servicetype.model.ServiceTypeResponse;

public class ServiceTypeMapper {

    public static ServiceType toServiceType(ServiceTypeCreateRequest request){
        ServiceType serviceType = new ServiceType();
        serviceType.setName(request.getName());
        serviceType.setDescription(request.getDescription());
        serviceType.setCode(request.getCode());
        serviceType.setSmsCode(request.getSmsCode());
        return serviceType;
    }

    public static ServiceTypeResponse toServiceTypeResponse(ServiceType serviceType){
        ServiceTypeResponse serviceTypeResponse = new ServiceTypeResponse();
        serviceTypeResponse.setId(serviceType.getId());
        serviceTypeResponse.setName(serviceType.getName());
        serviceTypeResponse.setDescription(serviceType.getDescription());
        serviceTypeResponse.setCode(serviceType.getCode());
        serviceTypeResponse.setSmsCode(serviceType.getSmsCode());
        return serviceTypeResponse;
    }
}
