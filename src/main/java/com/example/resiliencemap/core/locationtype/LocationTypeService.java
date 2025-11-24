package com.example.resiliencemap.core.locationtype;

import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.locationtype.model.LocationTypeCreateRequest;
import com.example.resiliencemap.core.locationtype.model.LocationTypeResponse;
import com.example.resiliencemap.core.locationtype.model.LocationTypeUpdateRequest;
import com.example.resiliencemap.functional.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationTypeService {
    private final LocationTypeRepository locationTypeRepository;

    public LocationTypeResponse createLocationType(LocationTypeCreateRequest locationTypeCreateRequest) {
        LocationType locationType = LocationTypeMapper.toLocationType(locationTypeCreateRequest);
        return LocationTypeMapper.toLocationTypeResponse(locationTypeRepository.save(locationType));
    }

    public LocationTypeResponse getLocationTypeResponse(Long id) {
        return LocationTypeMapper.toLocationTypeResponse(getLocationType(id));
    }

    public LocationType getLocationType(Long id) {
        Optional<LocationType> optional = locationTypeRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("LocationType not found");
        }
    }

    public List<LocationTypeResponse> getLocationTypes() {
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        return locationTypeList.stream()
                .map(LocationTypeMapper::toLocationTypeResponse).toList();
    }

    public LocationTypeResponse updateLocationType(Long locationTypeId, LocationTypeUpdateRequest request) {
        LocationType locationType = getLocationType(locationTypeId);
        locationType.setCode(request.getCode());
        locationType.setName(request.getName());
        locationType.setDescription(request.getDescription());
        locationType.setSmsCode(request.getSmsCode());
        return LocationTypeMapper.toLocationTypeResponse(locationTypeRepository.save(locationType));
    }

    public void deleteLocationType(Long id) {
        LocationType locationType = getLocationType(id);
        locationTypeRepository.delete(locationType);
    }

    public LocationType getUserLocationType() {
        Optional<LocationType> optional = locationTypeRepository.findByCode("private_initiative"); // TODO
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return createUserLocationtype();
        }
    }

    private LocationType createUserLocationtype() {
        LocationType locationType = new LocationType();
        locationType.setCode("private_initiative");
        locationType.setSmsCode("u");
        locationType.setName("Створено користувачем");
        locationType.setDescription("Точка, створена користувачем");
        return locationTypeRepository.save(locationType);
    }

}
