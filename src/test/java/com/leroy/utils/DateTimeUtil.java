package com.leroy.utils;

import com.leroy.core.configuration.Log;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    /**
     * Check if the date matches the format
     *
     * @param dateString - string requiring verification
     * @param dateFormat - expected date format
     * @return boolean
     */
    public static boolean isDateMatchFormat(String dateString, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            Date parsedDate = simpleDateFormat.parse(dateString);
            return simpleDateFormat.format(parsedDate).equals(dateString);
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    public static LocalDate strToLocalDate(String dateString, String dateFormat) {
        if (dateString == null)
            return null;
        try {
            if (dateString.toLowerCase().equals("сегодня"))
                return LocalDate.now();
            if (dateString.toLowerCase().equals("завтра"))
                return LocalDate.now().plusDays(1);
            Date date = new SimpleDateFormat(dateFormat, new Locale("ru", "RU")).
                    parse(dateString);
            return LocalDate.of(date.getYear() == 70 ? LocalDate.now().getYear() : date.getYear() + 1900,
                    date.getMonth() + 1, date.getDate());
        } catch (ParseException err) {
            Log.error("strToLocalDate() method: " + err.getMessage());
            return null;
        }
    }

    public static LocalDateTime strToLocalDateTime(String dateString, String dateFormat) {
        try {
            if (dateString.toLowerCase().contains("сегодня")) {
                LocalTime time = LocalTime.parse(StringUtils.substringAfter(dateString, ",").trim(),
                        DateTimeFormatter.ofPattern(StringUtils.substringAfter(dateFormat, ",").trim()));
                return LocalDateTime.of(LocalDate.now(), time);
            }
            Date date = new SimpleDateFormat(dateFormat, new Locale("ru", "RU")).
                    parse(dateString);
            return LocalDateTime.of(date.getYear() == 70 ? LocalDate.now().getYear() : date.getYear() + 1900,
                    date.getMonth() + 1, date.getDate(),
                    date.getHours(), date.getMinutes());
        } catch (ParseException err) {
            Log.error("strToLocalDateTime() method: " + err.getMessage());
            return null;
        }
    }

}
