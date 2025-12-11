package com.example.resiliencemap.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundSmsDto {
    private String id;
    private String to;
    private String text;
}
