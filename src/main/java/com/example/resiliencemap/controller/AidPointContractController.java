package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.contact.AidPointContactService;
import com.example.resiliencemap.core.contact.model.AidPointContactCreateRequest;
import com.example.resiliencemap.core.contact.model.AidPointContactDetailResponse;
import com.example.resiliencemap.core.contact.model.AidPointContactResponse;
import com.example.resiliencemap.core.user.model.User;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AidPointContractController {

    private final AidPointContactService aidPointContactService;

    @GetMapping("/aid-point/{aidPointId}/contacts")
    public List<AidPointContactResponse> getAidPointContacts(
            @PathVariable("aidPointId") Long aidPointId,
            @RequestParam(value = "searchData", defaultValue = "") String searchData,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @AuthenticationPrincipal User user) {
        return aidPointContactService.getAidPointContacts(aidPointId, searchData, page, pageSize, user);
    }

    @GetMapping("/aid-point-contact/all")
    public List<AidPointContactDetailResponse> getAllAidPointContacts(
            @RequestParam(value = "searchData", defaultValue = "") String searchData,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @AuthenticationPrincipal User user) {
        return aidPointContactService.getAllAidPointContacts(searchData, page, pageSize, user);
    }

    @GetMapping("/aid-point-contact/{contactId}")
    public AidPointContactResponse getAidPointContact(@PathVariable("contactId") Long contactId,
                                                      @AuthenticationPrincipal User user) {
        return aidPointContactService.getAidPointContactResponse(contactId, user);
    }

    @PostMapping("/aid-point/{aidPointId}/contact")
    public AidPointContactResponse addAidPointContact(@RequestBody @Valid AidPointContactCreateRequest request,
                                                      @AuthenticationPrincipal User user) {
        return aidPointContactService.addAidPointContact(request, user);
    }

    @DeleteMapping("/aid-point-contact/{contactId}")
    public ResponseEntity<Void> deleteAidPointContact(@PathVariable("contactId") Long contactId,
                                                      @AuthenticationPrincipal User user) {
        aidPointContactService.deleteAidPointContact(contactId, user);
        return ResponseEntity.ok().build();
    }
}
