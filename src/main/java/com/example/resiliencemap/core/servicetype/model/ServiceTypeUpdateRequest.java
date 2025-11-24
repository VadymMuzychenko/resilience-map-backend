package com.example.resiliencemap.core.servicetype.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceTypeUpdateRequest {
    private Long id;
    private String code;
    private String smsCode;
    private String name;
    private String description;
}
