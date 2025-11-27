package com.example.resiliencemap.functional;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringActiveProfile {

    private final Environment env;

    public boolean isDev() {
        return env.acceptsProfiles(Profiles.of("dev"));
    }
}
