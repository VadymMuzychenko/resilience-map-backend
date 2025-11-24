package com.example.resiliencemap.functional.utils;

import java.util.regex.Pattern;

public class ValidationUtil {

    public static boolean isEmailValid(String email) {
        return email != null && patternMatches(email, "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isPhoneNumberValid(String phone) {
        return phone != null && patternMatches(phone, "^(?:\\+380|380)[3-9][0-9]{8}$");
    }

    private static boolean patternMatches(String regex, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(regex)
                .matches();
    }
}
