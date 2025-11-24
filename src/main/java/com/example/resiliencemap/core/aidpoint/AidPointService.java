package com.example.resiliencemap.core.aidpoint;

import com.example.resiliencemap.core.aidpoint.model.*;
import com.example.resiliencemap.core.contact.AidPointContactMapper;
import com.example.resiliencemap.core.contact.AidPointContactRepository;
import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.locationtype.LocationTypeMapper;
import com.example.resiliencemap.core.locationtype.LocationTypeService;
import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.servicetype.ServiceTypeMapper;
import com.example.resiliencemap.core.servicetype.ServiceTypeService;
import com.example.resiliencemap.core.servicetype.model.ServiceType;
import com.example.resiliencemap.core.user.UserService;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.exception.ForbiddenException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AidPointService {

    private static final Double MAX_ALLOWED_RADIUS = 4_000.0;

    private final AidPointRepository aidPointRepository;
    private final LocationTypeService locationTypeService;
    private final AidPointContactRepository aidPointContactRepository;
    private final AidPointContactMapper aidPointContactMapper;
    private final UserService userService;
    private final ServiceTypeService serviceTypeService;

    public AidPoint createAndSaveAidPoint(AidPointCreateRequest request, User user) {

        AidPoint aidPoint = new AidPoint();
        aidPoint.setName(request.getName());
        aidPoint.setDescription(request.getDescription());
        aidPoint.setStatus(AidPointStatus.APPROVED);

        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(request.getLocation().getLongitude(), request.getLocation().getLatitude()));
        point.setSRID(4326);
        aidPoint.setLocation(point);

        aidPoint.setCreatedBy(user);
        aidPoint.setShowPoint(true);
        aidPoint.setCreatedAt(OffsetDateTime.now());
        aidPoint.setUpdatedAt(OffsetDateTime.now());

        if (User.UserRole.USER.equals(user.getRole())) {
            aidPoint.setLocationType(locationTypeService.getUserLocationType());
        } else {
            LocationType locationType = locationTypeService.getLocationType(request.getLocationTypeId());
            aidPoint.setLocationType(locationType);
        }

        return aidPointRepository.save(aidPoint);
    }

    public AidPointDetailResponse addAidPoint(AidPointCreateRequest request, User user) {
        AidPoint aidPoint = createAndSaveAidPoint(request, user);
        return toAidPointDetailResponse(aidPoint, user); // TODO: response without contacts?
    }

    public List<AidPointLightweightResponse> getAidPointsWithinRadius(Double latitude, Double longitude, Double radius) {
        if (radius > MAX_ALLOWED_RADIUS) {
            radius = MAX_ALLOWED_RADIUS;
        }
        List<AidPoint> aidPoints = aidPointRepository.findAidPointsWithinRadiusForUser(longitude, latitude, radius);
        return aidPoints.stream().map(this::toAidPointLightweightResponse).toList();
    }

    public AidPoint getAidPointById(Long id) {
        Optional<AidPoint> aidPoint = aidPointRepository.findById(id);
        return aidPoint.orElseThrow(() -> new NotFoundException("AidPoint Not Found"));
    }

    public AidPointDetailResponse getAidPoint(Long id, User user) {
        AidPoint aidPoint = getAidPointById(id);
        return toAidPointDetailResponse(aidPoint, user);
    }

    private AidPointDetailResponse toAidPointDetailResponse(AidPoint aidPoint, User user) {
        AidPointDetailResponse detailResponse = new AidPointDetailResponse();
        detailResponse.setId(aidPoint.getId());
        detailResponse.setName(aidPoint.getName());
        detailResponse.setDescription(aidPoint.getDescription());
        detailResponse.setLocationType(LocationTypeMapper.toLocationTypeResponse(aidPoint.getLocationType()));
        detailResponse.setLocation(new LocationCoordinates(aidPoint.getLocation()));
        detailResponse.setCreatedAt(aidPoint.getCreatedAt());
        detailResponse.setUpdatedAt(aidPoint.getUpdatedAt());
        detailResponse.setAddress(aidPoint.getAddress());
        List<AidPointContactResponse> contacts = aidPointContactRepository.findByAidPoint_Id(aidPoint.getId()).stream()
                .map(aidPointContact -> aidPointContactMapper.toAidPointContactResponse(aidPointContact, user)).toList();
        detailResponse.setContacts(contacts);

        detailResponse.setServices(aidPoint.getServiceTypes().stream().map(ServiceTypeMapper::toServiceTypeResponse).toList());
        detailResponse.setCreatedBy(userService.getUserResponse(aidPoint.getCreatedBy()));
        return detailResponse;
    }

    private AidPointLightweightResponse toAidPointLightweightResponse(AidPoint aidPoint) {
        AidPointLightweightResponse lightweightResponse = new AidPointLightweightResponse();
        lightweightResponse.setId(aidPoint.getId());
        lightweightResponse.setName(aidPoint.getName());
        lightweightResponse.setDescription(aidPoint.getDescription());
        lightweightResponse.setLocationType(LocationTypeMapper.toLocationTypeResponse(aidPoint.getLocationType()));
        lightweightResponse.setLocation(new LocationCoordinates(aidPoint.getLocation()));
        lightweightResponse.setAddress(aidPoint.getAddress());
        return lightweightResponse;
    }

    public AidPointDetailResponse addServiceType(Long aidPointId,Long serviceTypeId, User user) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        ServiceType serviceType = serviceTypeService.getServiceTypeFromRepository(serviceTypeId);
        if (!(User.UserRole.ADMIN.equals(user.getRole()) || aidPoint.getCreatedBy().getId().equals(user.getId()))) {
            throw new ForbiddenException("Access denied");
        }
        aidPoint.getServiceTypes().add(serviceType);
        AidPoint savedAidPoint = aidPointRepository.save(aidPoint);
        return toAidPointDetailResponse(savedAidPoint, user);
    }
}
