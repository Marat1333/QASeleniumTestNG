package com.leroy.utils;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;

public class RandomUtil {

    public static String randomPinCode(boolean pickup) {
        String generatedPinCode;
        do {
            generatedPinCode = pickup ? RandomStringUtils.randomNumeric(5) :
                    "9" + RandomStringUtils.randomNumeric(4);
        } while (pickup == generatedPinCode.startsWith("9"));
        return generatedPinCode;
    }

    public static String randomPhoneNumber() {
        return "+7" + RandomStringUtils.randomNumeric(10);
    }

    public static String randomEmail() {
        return RandomStringUtils.randomAlphabetic(6) + "@automail.com";
    }

    public static String randomCyrillicCharacters(int count) {
        String cyrillicCharacters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            result.append(cyrillicCharacters.charAt(
                    new Random().nextInt(cyrillicCharacters.length())));
        }
        return result.toString();
    }

}
