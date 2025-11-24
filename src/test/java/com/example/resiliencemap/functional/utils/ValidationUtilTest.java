package com.example.resiliencemap.functional.utils;

import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationUtilTest {

    @Test
    public void validatePhoneNumber() {
        Assertions.assertTrue(ValidationUtil.isPhoneNumberValid("+380600000000"));
        Assertions.assertTrue(ValidationUtil.isPhoneNumberValid("380600000000"));
        Assertions.assertFalse(ValidationUtil.isPhoneNumberValid(null));
        Assertions.assertFalse(ValidationUtil.isPhoneNumberValid(""));
        Assertions.assertFalse(ValidationUtil.isPhoneNumberValid("600000000"));
        Assertions.assertFalse(ValidationUtil.isPhoneNumberValid("phoneNumber"));
    }

    @Email
    @Test
    public void validateEmail() {
        Assertions.assertTrue(ValidationUtil.isEmailValid("user@email.com"));
        Assertions.assertTrue(ValidationUtil.isEmailValid("user.name@email.com"));
        Assertions.assertFalse(ValidationUtil.isEmailValid(null));
        Assertions.assertFalse(ValidationUtil.isEmailValid(""));
        Assertions.assertFalse(ValidationUtil.isPhoneNumberValid("name@email"));
        Assertions.assertFalse(ValidationUtil.isEmailValid("useremail.com"));
    }
}
