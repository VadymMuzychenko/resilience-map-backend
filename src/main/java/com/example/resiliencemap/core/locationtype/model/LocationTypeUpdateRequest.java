package com.example.resiliencemap.core.locationtype.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationTypeUpdateRequest {
    private Long id;
    private String code;
    private String smsCode;
    private String name;
    private String description;
}
