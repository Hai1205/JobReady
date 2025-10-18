package com.example.authservice.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class Utils {
    private static final String NUMERIC_STRING = "0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOTP(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(NUMERIC_STRING.length());
            char randomChar = NUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

    public static String generatePassword(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }
}