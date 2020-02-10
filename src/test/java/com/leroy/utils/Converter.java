package com.leroy.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Converter {

    /**
     * Convert String to Double and cut off non-digits if necessary
     *
     * @param str - string value
     * @return double
     */
    public static Double strToDouble(String str) throws ParseException {
        if (str == null)
            return null;
        return NumberFormat.getInstance(Locale.FRANCE)
                .parse(str.replaceAll("[^\\d+\\,]", "")).doubleValue();
    }

    public static Integer strToInt(String str) {
        if (str == null)
            return null;
        return Integer.parseInt(str.replaceAll("\\D+", ""));
    }
}
