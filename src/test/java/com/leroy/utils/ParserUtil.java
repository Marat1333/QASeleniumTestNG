package com.leroy.utils;

import com.leroy.core.configuration.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ParserUtil {

    /**
     * Convert String to Double and cut off non-digits if necessary
     *
     * @param str - string value
     * @return double
     */
    public static Double strToDouble(String str) {
        if (str == null)
            return null;
        try {
            return NumberFormat.getInstance(Locale.FRANCE)
                    .parse(str.replaceAll("[^\\d+\\,\\-]", "")).doubleValue();
        } catch (ParseException err) {
            Log.error(err.getMessage());
            return null;
        }
    }

    public static Integer strToInt(String str) {
        if (str == null)
            return null;
        return Integer.parseInt(str.replaceAll("\\D+", ""));
    }

    public static String strWithOnlyDigits(String str) {
        if (str == null)
            return null;
        return str.replaceAll("\\D+", "");
    }

    public static String prettyDoubleFmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static Double sum(double a, double b, int decimalPlaces) {
        return Math.round((a + b) * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

    public static String standardPhoneFmt(String phoneNumber) {
        if (!phoneNumber.startsWith("+7"))
            phoneNumber = "+7" + phoneNumber;
        return phoneNumber.replaceAll(" |-", "");
    }
}
