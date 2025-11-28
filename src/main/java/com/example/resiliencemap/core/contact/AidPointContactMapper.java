package com.example.resiliencemap.core.contact;

import com.example.resiliencemap.core.contact.model.AidPointContact;
import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AidPointContactMapper {

    private final AidPointContactRepository aidPointContactRepository;

    public AidPointContactResponse toAidPointContactResponse(AidPointContact aidPointContact, User user) {
        AidPointContactResponse response = new AidPointContactResponse();
        response.setId(aidPointContact.getId());
        response.setFullName(aidPointContact.getFullName());
        response.setPhoneNumber(aidPointContact.getPhoneNumber());
        response.setRole(aidPointContact.getRole());
        if (User.UserRole.ADMIN.equals(user.getRole()) || User.UserRole.MODERATOR.equals(user.getRole())) {
            response.setHide(aidPointContact.getHide());
            response.setCreatedAt(aidPointContact.getCreatedAt());
        } else if (aidPointContactRepository.existsByAidPoint_CreatedBy_Id(user.getId())) {
            response.setHide(aidPointContact.getHide());
            response.setCreatedAt(aidPointContact.getCreatedAt());
        }
        return response;
    }
}
