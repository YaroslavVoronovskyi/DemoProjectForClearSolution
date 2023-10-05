package com.gmail.voronovskyi.yaroslav.demo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.validator.EmailValidator;

@Getter
@Setter
public class Utils {

    public static boolean isValidEmailAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}

