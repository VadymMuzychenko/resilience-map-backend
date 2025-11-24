package com.example.resiliencemap.core.contact.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AidPointContactDetailResponse extends  AidPointContactResponse {
    private Long aidPointId;

    public AidPointContactDetailResponse(AidPointContactResponse response, Long aidPointId) {
        this.setId(response.getId());
        this.setFullName(response.getFullName());
        this.setPhoneNumber(response.getPhoneNumber());
        this.setRole(response.getRole());
        this.setHide(response.getHide());
        this.setCreatedAt(response.getCreatedAt());
        this.aidPointId = aidPointId;
    }
}
