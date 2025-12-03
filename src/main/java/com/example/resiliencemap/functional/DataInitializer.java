package com.example.resiliencemap.functional;

import com.example.resiliencemap.core.aidpoint.AidPointService;
import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.aidpoint.model.AidPointCreateRequest;
import com.example.resiliencemap.core.aidpoint.model.LocationCoordinates;
import com.example.resiliencemap.core.comment.CommentService;
import com.example.resiliencemap.core.comment.model.CommentCreateRequest;
import com.example.resiliencemap.core.contact.AidPointContactService;
import com.example.resiliencemap.core.contact.model.AidPointContactCreateRequest;
import com.example.resiliencemap.core.locationtype.LocationTypeService;
import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.servicetype.ServiceTypeRepository;
import com.example.resiliencemap.core.servicetype.model.ServiceType;
import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final AidPointService aidPointService;
    private final ServiceTypeRepository serviceTypeRepository;
    private final CommentService commentService;
    private final AidPointContactService aidPointContactService;
    private final LocationTypeService locationTypeService;

    @Transactional
    protected void loadTestData() {
        LocationType locationType = locationTypeService.getLocationTypeByCode("WARM_POINT");
        User admin = userRepository.findById(1L).get();

        AidPointCreateRequest request = new AidPointCreateRequest();
        request.setName("Пункт Незламності №12");
        request.setDescription("Працює з 08:00 до 20:00. Є генератор, інтернет.");
        request.setLocationTypeId(locationType.getId());
        request.setAddress("м. ..., вул. ... 12");
        request.setLocation(new LocationCoordinates(0.000123, 0.000123));
        request.setShowPoint(true);
        AidPoint aidPoint1 = aidPointService.createAndSaveAidPoint(request, admin);

        AidPointCreateRequest request2 = new AidPointCreateRequest();
        request2.setName("Пункт Незламності №11");
        request2.setDescription("Працює з 08:00 до 20:00. Є генератор, інтернет.");
        request2.setLocationTypeId(locationType.getId());
        request2.setAddress("м. ..., вул. ... 11");
        request2.setLocation(new LocationCoordinates(0.000789, 0.000789));
        request2.setShowPoint(true);
        AidPoint aidPoint2 = aidPointService.createAndSaveAidPoint(request2, admin);

        ServiceType waterService = serviceTypeRepository.getServiceTypeByCode("WATER");
        if (waterService != null) {
            aidPointService.addServiceType(aidPoint1.getId(), waterService.getId(), admin);
        }

        ServiceType powerService = serviceTypeRepository.getServiceTypeByCode("POWER");
        if (waterService != null) {
            aidPointService.addServiceType(aidPoint1.getId(), powerService.getId(), admin);
        }

        ServiceType heatingService = serviceTypeRepository.getServiceTypeByCode("HEATING");
        if (waterService != null) {
            aidPointService.addServiceType(aidPoint1.getId(), heatingService.getId(), admin);
        }

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setText("Чудовий пункт, дуже тепло і є чай!");
        commentCreateRequest.setRating((short) 5);
        commentService.addComment(aidPoint1.getId(), commentCreateRequest, admin);

        CommentCreateRequest commentCreateRequest2 = new CommentCreateRequest();
        commentCreateRequest2.setText("Дуже допомогли! Дякую волонтерам!");
        commentCreateRequest2.setRating((short) 5);
        commentService.addComment(aidPoint1.getId(), commentCreateRequest2, admin);

        AidPointContactCreateRequest contactCreateRequest = new AidPointContactCreateRequest();
        contactCreateRequest.setAidPointId(aidPoint1.getId());
        contactCreateRequest.setFullName("Іван Петренко");
        contactCreateRequest.setPhoneNumber("+380601112233");
        contactCreateRequest.setRole("Волонтер");
        aidPointContactService.addAidPointContact(contactCreateRequest, admin);
    }
}
