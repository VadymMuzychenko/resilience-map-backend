package com.example.resiliencemap.core.contact;

import com.example.resiliencemap.core.aidpoint.AidPointService;
import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.contact.model.AidPointContact;
import com.example.resiliencemap.core.contact.model.AidPointContactCreateRequest;
import com.example.resiliencemap.core.contact.model.AidPointContactDetailResponse;
import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.exception.ConflictException;
import com.example.resiliencemap.functional.exception.ForbiddenException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AidPointContactService {

    private static final int MAX_PAGE_SIZE = 50;

    private final AidPointContactRepository aidPointContactRepository;
    private final AidPointService aidPointService;
    private final AidPointContactMapper aidPointContactMapper;

    public AidPointContactResponse getAidPointContactResponse(Long id, User user) {
        AidPointContact contact = getContactFromRepository(id);
        return aidPointContactMapper.toAidPointContactResponse(contact, user);
    }

    private AidPointContact getContactFromRepository(Long id) {
        Optional<AidPointContact> optional = aidPointContactRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("AidPointContact not found");
        }
    }

    public List<AidPointContactResponse> getAidPointContacts(Long id, String searchData, int page, int pageSize, User user) {
        List<AidPointContact> aidPointContacts;
        if (User.UserRole.ADMIN.equals(user.getRole()) || User.UserRole.MODERATOR.equals(user.getRole())) {
            aidPointContacts = aidPointContactRepository.findContactsForAdmin(id, searchData);
        } else {
            aidPointContacts = aidPointContactRepository.findContactsForUser(id, user.getId(), searchData);
        }
        return aidPointContacts.stream().map(aidPointContact ->
                aidPointContactMapper.toAidPointContactResponse(aidPointContact, user)
        ).toList();
    }

    public AidPointContactResponse addAidPointContact(AidPointContactCreateRequest request, User user) {
        AidPointContact aidPointContact = new AidPointContact();
        AidPoint aidPoint = aidPointService.getAidPointById(request.getAidPointId());
        if (aidPointContactRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ConflictException("AidPointContact with this number already exists");
        }
        aidPointContact.setFullName(request.getFullName());
        aidPointContact.setPhoneNumber(request.getPhoneNumber());
        aidPointContact.setRole(request.getRole());
        aidPointContact.setHide(request.getIsHide());
        aidPointContact.setAidPoint(aidPoint);
        aidPointContact.setCreatedAt(OffsetDateTime.now());
        return aidPointContactMapper.toAidPointContactResponse(aidPointContact, user);
    }

    public List<AidPointContactDetailResponse> getAllAidPointContacts(String searchData, int page, int pageSize, User user) {
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        if (!(User.UserRole.ADMIN.equals(user.getRole()) || User.UserRole.MODERATOR.equals(user.getRole()))) {
            throw new ForbiddenException("Access denied");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        List<AidPointContact> aidPointContacts = aidPointContactRepository.findAllContacts(searchData, pageable);
        return aidPointContacts.stream().map(aidPointContact -> {
            AidPointContactResponse response = aidPointContactMapper.toAidPointContactResponse(aidPointContact, user);
            return new AidPointContactDetailResponse(response, aidPointContact.getAidPoint().getId());
        }).toList();
    }

    public void deleteAidPointContact(Long aidPointId, User user) {
        AidPointContact aidPointContact = getContactFromRepository(aidPointId);
        if (User.UserRole.ADMIN.equals(user.getRole()) || aidPointContactRepository.existsByAidPoint_CreatedBy_Id(user.getId())) {
            aidPointContactRepository.delete(aidPointContact);
        } else {
            throw new ForbiddenException("Access denied");
        }
    }
}