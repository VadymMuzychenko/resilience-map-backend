package com.example.resiliencemap.core.aidpoint;

import com.example.resiliencemap.core.aidpoint.model.*;
import com.example.resiliencemap.core.contact.AidPointContactMapper;
import com.example.resiliencemap.core.contact.AidPointContactRepository;
import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.locationtype.LocationTypeMapper;
import com.example.resiliencemap.core.locationtype.LocationTypeService;
import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.locationtype.model.LocationTypeChangeRequest;
import com.example.resiliencemap.core.photo.service.PhotoService;
import com.example.resiliencemap.core.servicetype.ServiceTypeMapper;
import com.example.resiliencemap.core.servicetype.ServiceTypeRepository;
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
import java.util.ArrayList;
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
    private final PhotoService photoService;
    private final ServiceTypeRepository serviceTypeRepository;

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
        List<Long> photosIds = addPhotos(aidPoint, request.getDraftPhotoIds());
        aidPoint = addServiceTypes(aidPoint, request.getServiceIds());
        return toAidPointDetailResponse(aidPoint, user, photosIds); // TODO: response without contacts?
    }

    private List<Long> addPhotos(AidPoint aidPoint, List<Long> draftPhotoIds) {
        List<Long> photosIds = new ArrayList<>();
        if (draftPhotoIds != null && !draftPhotoIds.isEmpty()) {
            photosIds = photoService.moveDraftPhotosToAidPointPhotos(draftPhotoIds, aidPoint);
        }
        return photosIds;
    }

    private AidPoint addServiceTypes(AidPoint aidPoint, List<Long> serviceIds) {
        if (serviceIds != null && !serviceIds.isEmpty()) {
            for (Long serviceId : serviceIds) {
                serviceTypeRepository.findById(serviceId).ifPresent(serviceType ->
                        aidPoint.getServiceTypes().add(serviceType));
            }
            return aidPointRepository.save(aidPoint);
        }
        return aidPoint;
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

    private AidPointDetailResponse toAidPointDetailResponse(AidPoint aidPoint, User user){
        List<Long> photoIds = photoService.getPhotoIDsByAidPoint(aidPoint.getId());
        return toAidPointDetailResponse(aidPoint, user, photoIds);
    }

    private AidPointDetailResponse toAidPointDetailResponse(AidPoint aidPoint, User user, List<Long> photoIds) {
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
        detailResponse.setPhotoIds(photoIds);
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

    public AidPointDetailResponse updateAidPoint(Long aidPointId, AidPointUpdateRequest request, User user) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        if (!User.UserRole.ADMIN.equals(user.getRole()) || !aidPoint.getCreatedBy().equals(user)) {
            throw new ForbiddenException("Access Denied");
        }
        aidPoint.setName(request.getName());
        aidPoint.setDescription(request.getDescription());
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(request.getLocation().getLongitude(), request.getLocation().getLatitude()));
        point.setSRID(4326);
        aidPoint.setLocation(point);
        aidPoint.setShowPoint(true);
        aidPoint.setUpdatedAt(OffsetDateTime.now());
        AidPoint savedAidPoint = aidPointRepository.save(aidPoint);
        return toAidPointDetailResponse(savedAidPoint, user);
    }

    public AidPointDetailResponse changeLocationType(Long aidPointId, LocationTypeChangeRequest request, User user) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        LocationType locationType = locationTypeService.getLocationType(request.getNewLocationTypeId());
        aidPoint.setLocationType(locationType);
        aidPoint.setUpdatedAt(OffsetDateTime.now());
        AidPoint savedAidPoint = aidPointRepository.save(aidPoint);
        return toAidPointDetailResponse(savedAidPoint, user);
    }

    public AidPointDetailResponse addServiceType(Long aidPointId, Long serviceTypeId, User user) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        ServiceType serviceType = serviceTypeService.getServiceTypeFromRepository(serviceTypeId);
        if (!(User.UserRole.ADMIN.equals(user.getRole()) || aidPoint.getCreatedBy().getId().equals(user.getId()))) {
            throw new ForbiddenException("Access denied");
        }
        aidPoint.getServiceTypes().add(serviceType);
        AidPoint savedAidPoint = aidPointRepository.save(aidPoint);
        return toAidPointDetailResponse(savedAidPoint, user);
    }

    public AidPointDetailResponse removeServiceType(Long aidPointId, Long serviceTypeId, User user) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        ServiceType serviceType = serviceTypeService.getServiceTypeFromRepository(serviceTypeId);
        if (!(User.UserRole.ADMIN.equals(user.getRole()) || aidPoint.getCreatedBy().getId().equals(user.getId()))) {
            throw new ForbiddenException("Access denied");
        }
        aidPoint.getServiceTypes().remove(serviceType);
        AidPoint savedAidPoint = aidPointRepository.save(aidPoint);
        return toAidPointDetailResponse(savedAidPoint, user);
    }

    public void deleteAidPoint(Long aidPointId) {
        AidPoint aidPoint = getAidPointById(aidPointId);
        aidPointRepository.delete(aidPoint);
    }
}
