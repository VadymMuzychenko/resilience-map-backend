package com.example.resiliencemap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResilienceMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResilienceMapApplication.class, args);
    }

}
