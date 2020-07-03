package com.leroy.core.configuration;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Deprecated
public class DateUtil {
    private static final SimpleDateFormat YYYY_MM_DD_HH_SS_MM_SSS = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss.SSS");
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
            "yyyy_MM_dd_hh_mm_ss");
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SSS = new SimpleDateFormat(
            "yyyy_MM_dd_hh_mm_ss_SSS");
    public static final SimpleDateFormat MM_DD_YYYY = new SimpleDateFormat(
            "MM/dd/yyyy");
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(
            "yyyy/MM/dd");
    public static final SimpleDateFormat MM_DD_YY = new SimpleDateFormat(
            "MM/dd/yy");
    private static final SimpleDateFormat MM_DD_YYYY_KKMM = new SimpleDateFormat(
            "MM/dd/yyyy kk:mm");
    private static final SimpleDateFormat YYYY_MM_DD_KKMMSS = new SimpleDateFormat(
            "yyyy-MM-dd kk:mm:ss");
    private static final SimpleDateFormat MM_DD_YYYY_HHMMSS_A = new SimpleDateFormat(
            "MM/dd/yyyy hh:mm:ss a");
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_S = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.S");
    private static final SimpleDateFormat H_MM_AM_PM = new SimpleDateFormat(
            "h:mm a");
    private static final SimpleDateFormat ISO_8601_DATE_TIME = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss"); // Ignore Time Zone
    public static final SimpleDateFormat MMM_DD_YYYY = new SimpleDateFormat(
            "MMM dd,yyyy");
    public static final SimpleDateFormat DAY_YYYY_MM_DD_HH_MM_SS_Z = new SimpleDateFormat(
            "E_yyyy.MM.dd_HH.mm.ss_z");


    /**
     * Constructor with scope access modifier marked protected to foil
     * instantiation but permit inheritance.
     */
    protected DateUtil() {
    }

    /**
     * Parses the given string into a {@link Date} for the format:
     * "11/03/2011 02:55:40 pm".
     *
     * @param strToParse String timestamp to parse
     * @return A parsed Date object
     * @throws ParseException
     */
    public static final Date parseDatePerFormat(String strToParse,
                                                SimpleDateFormat format) throws ParseException {
        return format.parse(strToParse);
    }

    public static final Date parseDateWithTimeZone(String strToParse)
            throws ParseException {
        YYYY_MM_DD_HH_SS_MM_SSS.setTimeZone(TimeZone.getTimeZone("UTC"));
        return YYYY_MM_DD_HH_SS_MM_SSS.parse(strToParse);
    }

    /**
     * Parses the given string into a {@link Date} for the format:
     * "11/03/2011 14:55:40".
     *
     * @param strToParse String timestamp to parse
     * @return A parsed Date object
     * @throws ParseException
     */
    public static final Date parseMilitaryWithSecondsDBDate(String strToParse)
            throws ParseException {
        return YYYY_MM_DD_KKMMSS.parse(strToParse);
    }

    /**
     * Parses the given string into a {@link Date} for the format:
     * "11/03/2011 02:55:40 pm".
     *
     * @param strToParse String timestamp to parse
     * @return A parsed Date object
     * @throws ParseException
     */
    public static final Date parseMonthDayYearHourMinSecAmPm(String strToParse)
            throws ParseException {
        return MM_DD_YYYY_HHMMSS_A.parse(strToParse);
    }

    /**
     * This method was created for the timestamp format on the Open Webmail
     * page. assumes time is today
     *
     * @param strTime String timestamp to parse
     * @return A parsed Date object
     * @throws ParseException
     */
    public static final Date parseMilitaryHourMin(String strTime)
            throws ParseException {
        return MM_DD_YYYY_KKMM.parse(getCurrentDate("MM/dd/yyyy") + " "
                + strTime);
    }

    public static String formatMonthDayYear(Date date) {
        return formatDate(date, MM_DD_YYYY);
    }

    public static String formatCurrentMonthDayYear() {
        return formatDate(new Date(), MM_DD_YYYY);
    }

    public static String formatCurrentYearMonthDay() {
        return formatDate(new Date(), YYYY_MM_DD);
    }

    public static String formatCurrentMonthDay2DigitYear() {
        return formatDate(new Date(), MM_DD_YY);
    }

    public static String formatCurrentDateTime() {
        return formatDate(new Date(), YYYY_MM_DD_HH_MM_SS);
    }

    public static String formatCurrentDateTimeSSS() {
        return formatDate(new Date(), YYYY_MM_DD_HH_MM_SS_SSS);
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, YYYY_MM_DD_HH_SS_MM_SSS);
    }

    public static String formatCurrentTime_H_MM_AM_PM() {
        return formatDate(new Date(), H_MM_AM_PM);
    }

    public static String formatCurrectDayYYYYMMDDHHMMSSTimeZone() {
        return formatDate(new Date(), DAY_YYYY_MM_DD_HH_MM_SS_Z);
    }

    public static String isoCurrentDateTime() {
        return formatDate(new Date(), ISO_8601_DATE_TIME);
    }

    public static String getCurrentTimeFormat(String format) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        return formatDate(new Date(), timeFormat);
    }

    public static String getPastDate(int days) {
        Date date = new Date();
        date = addDays(date, days * -1);
        return formatDate(date, MM_DD_YYYY_HHMMSS_A).trim();
    }

    public static String getCurrentDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date).trim();
    }

    public static String formatDate(Date date, SimpleDateFormat formatter) {
        return formatter.format(date);
    }

    public static Date addDays(Date d, int days) {
        d.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);
        return d;
    }

    public static Date subtractDays(Date d, int days) {
        d.setTime(d.getTime() - days * 1000 * 60 * 60 * 24);
        return d;
    }

    public static String getPastMonth() {

        SimpleDateFormat format = new SimpleDateFormat("MM");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return format.format(Calendar.MONTH).trim();
    }

    public static String getCurrentMonth() {

        SimpleDateFormat format = new SimpleDateFormat("MM");
        Date date = new Date();
        return format.format(date).trim();
    }

    public static String getCurrentYear() {

        SimpleDateFormat format = new SimpleDateFormat("YYYY");
        Date date = new Date();
        return format.format(date).trim();
    }

    public static String getCurrentDay() {

        SimpleDateFormat format = new SimpleDateFormat("DD");
        Date date = new Date();
        return format.format(date).trim();
    }

    public static String getPastYear() {

        SimpleDateFormat format = MM_DD_YY;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return format.format(cal.getTime()).trim();
    }

    /**
     * Validate that date1 is older than date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareTwoDates(Date date1, Date date2) {

        boolean isOlder = false;
        if (date1.before(date2)) {
            isOlder = true;
        }
        return isOlder;
    }

    public static final Date parseDate(String strTime) throws ParseException {
        SimpleDateFormat format = MM_DD_YY;
        Date date = format.parse(strTime);
        return date;
    }

    public static String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month];
        return date;
    }

    /**
     * This will return the current date and time in the format of 'October 27,
     * 2015 4:40 PM'
     *
     * @param args
     * @return
     */
    public static String getMonthDateYearTime(String[] args) {
        Date now = new Date(); // New Date object will be initialized to the
        // current time.
        String date = (DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.SHORT).format(now));
        return date;
    }

    /**
     * This will return the current date and time in the format of 'October 29,
     * 2015'
     *
     * @param args
     * @return
     */
    public static String getDateMonthLong(String[] args) {
        Date now = new Date(); // New Date object will be initialized to the
        // current time.
        String date = (DateFormat.getDateInstance(DateFormat.LONG).format(now));
        return date;
    }

}

