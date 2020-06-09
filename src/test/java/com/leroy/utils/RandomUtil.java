package com.leroy.utils;

import org.apache.commons.lang.RandomStringUtils;

public class RandomUtil {

    public static String randomPinCode(boolean pickup) {
        String generatedPinCode;
        do {
            generatedPinCode = pickup ? RandomStringUtils.randomNumeric(5) :
                    "9" + RandomStringUtils.randomNumeric(4);
        } while (pickup == generatedPinCode.startsWith("9"));
        return generatedPinCode;
    }

}
