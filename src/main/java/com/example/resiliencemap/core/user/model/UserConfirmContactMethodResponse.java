package com.example.resiliencemap.core.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserConfirmContactMethodResponse {
    private String message;
}
