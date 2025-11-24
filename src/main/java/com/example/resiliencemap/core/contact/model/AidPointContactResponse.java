package com.example.resiliencemap.core.contact.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AidPointContactResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String role;
    private Boolean hide;
    private OffsetDateTime createdAt;
}
