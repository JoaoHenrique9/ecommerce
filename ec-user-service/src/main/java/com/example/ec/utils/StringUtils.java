package com.example.ec.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class StringUtils {

    public static String encoder(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

}
