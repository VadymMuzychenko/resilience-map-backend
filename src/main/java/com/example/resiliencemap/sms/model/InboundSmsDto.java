package com.example.resiliencemap.sms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundSmsDto {
    private String id;
    private String from;
    private String timestamp;
    private String text;
}
