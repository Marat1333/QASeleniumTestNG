package com.leroy.utils;

import com.leroy.core.configuration.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Convert Double to String
     *
     * @param val - double value
     * @return String
     */
    public static String doubleToStr(Double val, int decimalPlaces, boolean displayDecimalIfInt) {
        if (val == null)
            return null;
        if (!displayDecimalIfInt && val == Math.round(val))
            return String.valueOf(val.intValue());
        return String.format("%." + decimalPlaces + "f", val);
    }

    /**
     * Convert String to Int and cut off non-digits if necessary
     *
     * @param str - string value
     * @return Integer
     */
    public static Integer strToInt(String str) {
        if (str == null)
            return null;
        return Integer.parseInt(str.replaceAll("[^\\d+\\,\\-]", ""));
    }

    /**
     * Leave only digits and remove any letters and other non-digit symbols including space character
     *
     * @param str - string (text)
     * @return string with only digits
     */
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

    /**
     * Sum double / float numbers and round the decimal part
     *
     * @param a             - the first number
     * @param b             - the second number
     * @param decimalPlaces - count of digits after the dot
     * @return Double
     */
    public static Double plus(double a, double b, int decimalPlaces) {
        return Math.round((a + b) * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

    public static Double minus(double a, double b, int decimalPlaces) {
        return plus(a, -b, decimalPlaces);
    }

    public static Double multiply(double a, double b, int decimalPlaces) {
        return Math.round((a * b) * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

    /**
     * Converts a phone number to just a number without spaces and "-"
     *
     * @param phoneNumber - phone number
     * @return String
     */
    public static String standardPhoneFmt(String phoneNumber) {
        if (!phoneNumber.startsWith("+7"))
            phoneNumber = "+7" + phoneNumber;
        return phoneNumber.replaceAll(" |-", "");
    }

    public static String parseFirstName(String fullName) {
        String[] nameArr = fullName.split(" ");
        if (nameArr.length > 0)
            return nameArr[0].trim();
        return null;
    }

    public static String parseLastName(String fullName) {
        String[] nameArr = fullName.split(" ");
        if (nameArr.length > 1)
            return nameArr[1].trim();
        return null;
    }

    /**
     * Выделяет из текста только email
     *
     * @param text - текст, содержащий в себе email
     * @return - только слово, содержащее email (первое попавшееся)
     */
    public static String extractEmailFromString(String text) {
        List<String> emails = extractEmailsFromString(text);
        return emails.size() > 0 ? emails.get(0) : null;
    }

    /**
     * Выделяет из текста только email'ы
     *
     * @param text - текст, содержащий в себе email(ы)
     * @return - список всех email в тексте
     */
    public static List<String> extractEmailsFromString(String text) {
        Matcher m = Pattern.compile("\\S+@[^\\s\\.]+\\.\\w+").matcher(
                text);
        List<String> emails = new ArrayList<>();
        while (m.find()) {
            emails.add(m.group());
        }
        return emails;
    }

    public static String replaceSpecialSymbols(String source) {
        if (source == null) {
            return null;
        }
        source = source.replaceAll("[*]", "").replaceAll("[\\s]{2,}", " ");
        if (source.equals("undefined")) {
            source = null;
        }
        return source;
    }

}
