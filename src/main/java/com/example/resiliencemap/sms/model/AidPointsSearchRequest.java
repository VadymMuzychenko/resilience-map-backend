package com.example.resiliencemap.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AidPointsSearchRequest {
    private double lat;
    private double lon;
    private String locationType;
    private Set<String> services;
    private int page;
}
