package com.leroy.core.configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.Perl5Compiler;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides convenience methods for common text manipulation and formatting
 * operations.
 */
@Deprecated
public class TextUtil extends StringUtils {
    public static final String REGEX_ALPHANUMERIC;
    public static final String REGEX_NUMERIC;
    public static final String REGEX_NUMERIC_ONLY;
    public static final String REGEX_WITHOUT_WHITESPACE;
    public static final String REGEX_SPACE_ALPHANUMERIC;
    public static final String REGEX_BARCODE;
    public static final String REGEX_US_PHONE;
    public static final String REGEX_PHONE;
    public static final String REGEX_EMAIL;
    public static final String DATE_REGEXP;
    public static final String TIMESTAMP_REGEXP;
    public static final String DATE_REGEXP1;
    public static final String DATE_DOB;
    public static final String DATE_REGEXP_SLASHDATE; // MM/DD/YY
    public static final String DATE_CCYY_REGEXP;
    public static final String MM_DD;
    public static final String MM_DD2;
    public static final String XML_CHAR_AND = "&#38;";
    public static final String REGEX_CAMEL_CASE = "^[A-Z][a-z]+";

    private static Perl5Util m_alphaNumericMatcher;
    private static Perl5Util m_numericMatcher;
    private static Perl5Util m_withoutwhitespaceMatcher;
    private static Perl5Util m_alphaNumericSpaceMatcher;
    private static Perl5Util m_usPhoneMatcher;
    private static Perl5Util m_emailMatcher;
    private static Perl5Util m_dateMatcher;
    private static Perl5Util m_barcodeMatcher;

    public static final SimpleDateFormat ISO_DATETIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    public static final SimpleDateFormat ISO_DATETIME_FORMAT_WITH_TZ = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    public static final SimpleDateFormat DATETIME_FORMAT_LONG = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    public static final SimpleDateFormat GMT_DATETIME_FORMAT_LONG = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    public static final SimpleDateFormat DATETIME_FORMAT_SHORT = new SimpleDateFormat(
            "MM/dd/yy h:mm a", Locale.US);
    public static final SimpleDateFormat DATETIME_FORMAT_SHORT_70 = new SimpleDateFormat(
            "MM/dd/yyyy h:mm a", Locale.US);
    public static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat(
            "MM/dd/yyyy", Locale.US);
    public static final SimpleDateFormat TIME_FORMAT_SHORT = new SimpleDateFormat(
            "h:mm a", Locale.US);
    public static final SimpleDateFormat TIME_FORMAT_24_HOUR = new SimpleDateFormat(
            "HH:mm", Locale.US);
    public static final String[] wildcardchars = new String[] { "*", "?" };

    static {
        REGEX_ALPHANUMERIC = "/^[A-Za-z0-9]+$/";
        REGEX_NUMERIC = "/^[0-9]+$/";
        REGEX_NUMERIC_ONLY = "[^0-9]";
        REGEX_WITHOUT_WHITESPACE = "/^[^\\s]+$/";
        REGEX_SPACE_ALPHANUMERIC = "/^[A-Za-z0-9\\s]+$/";
        REGEX_BARCODE = "/^[A-Za-z0-9\\s\\-\\.\\+\\%\\!\\#\\{\\}\\(\\)\\*\\,\\^\\_\\`\\~\\|\\[\\]\\'\\134\\042]+$/";
        REGEX_US_PHONE = "/^((\\(\\d{3}\\) ?)|(\\d{3}-))?\\d{3}-\\d{4}$/";
        REGEX_EMAIL = "/^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$/";
        DATE_REGEXP = "m!^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$!";
        REGEX_PHONE = "/^[A-Za-z0-9\\(\\)\\+\\.\\-]/";
        TIMESTAMP_REGEXP = "m!^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d( )(0?[0-9]|1[0-9]|2[0-3])[:](0?[0-9]|[1|2|3|4|5][0-9])[:](0?[0-9]|[1|2|3|4|5][0-9])$!";

        DATE_REGEXP1 = "m!^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.]\\d\\d$!";
        DATE_DOB = "m!^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](18|19|20|21)\\d\\d$!";
        DATE_REGEXP_SLASHDATE = "m!^(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/\\d\\d$!";
        DATE_CCYY_REGEXP = "m!^(19|20)\\d\\d$!";
        // MM_DD = "/^[0-9]+$/";
        MM_DD = "m!^(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])$!";
        // "/^\\d{2}/\\d{2}$/"; // "/\\d{2}-\\d{2}/";
        MM_DD2 = "m!^(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$!";

        int compileOptions = Perl5Compiler.DEFAULT_MASK
                | Perl5Compiler.READ_ONLY_MASK;
        PatternCache patternCache = new PatternCacheLRU(new Perl5Compiler());
        patternCache.getPattern(REGEX_ALPHANUMERIC, compileOptions);
        patternCache.getPattern(REGEX_NUMERIC, compileOptions);
        patternCache.getPattern(REGEX_WITHOUT_WHITESPACE, compileOptions);
        patternCache.getPattern(REGEX_SPACE_ALPHANUMERIC, compileOptions);
        patternCache.getPattern(REGEX_BARCODE, compileOptions);
        patternCache.getPattern(REGEX_US_PHONE, compileOptions);
        patternCache.getPattern(REGEX_EMAIL, compileOptions);
        patternCache.getPattern(DATE_REGEXP, compileOptions);
        patternCache.getPattern(TIMESTAMP_REGEXP, compileOptions);
        patternCache.getPattern(DATE_DOB, compileOptions);
        patternCache.getPattern(MM_DD, compileOptions);
        patternCache.getPattern(MM_DD2, compileOptions);
        m_alphaNumericMatcher = new Perl5Util(patternCache);
        m_numericMatcher = new Perl5Util(patternCache);
        m_withoutwhitespaceMatcher = new Perl5Util(patternCache);
        m_alphaNumericSpaceMatcher = new Perl5Util(patternCache);
        m_barcodeMatcher = new Perl5Util(patternCache);
        m_usPhoneMatcher = new Perl5Util(patternCache);
        m_emailMatcher = new Perl5Util(patternCache);
        m_dateMatcher = new Perl5Util(patternCache);

        GMT_DATETIME_FORMAT_LONG.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * @param text
     * @return true if trimmed text is not null or empty
     */
    public static boolean hasValue(String text) {
        return isNotNullNotEmpty(text, true);
    }

    /**
     * Replace each token seperated by <seperator> from <input> with
     * <replaceWith>.
     *
     * @param input
     *            Input text variable
     * @param seperator
     * @param replaceWith
     * @return
     */
    public static String replaceTokens(String input, String seperator,
                                       String replaceWith) {
        StringBuffer buffer = new StringBuffer();
        if ((input != null) && (input.length() > 0) && (replaceWith != null)) {
            StringTokenizer stringTokenizer = new StringTokenizer(input,
                    seperator);
            int nTokens = stringTokenizer.countTokens();
            for (int i = 0; i < nTokens; i++) {
                if (i > 0)
                    buffer.append(seperator);
                buffer.append(replaceWith);
            }
        }
        return buffer.toString();
    }

    public static String replaceAll(String input, char toReplace,
                                    String replaceWith) {
        if ((input == null) || (input.length() == 0) || (replaceWith == null)) {
            return input;
        }

        int fromIndex = 0;
        int index = -1;
        StringBuffer buffer = new StringBuffer();

        while ((index = input.indexOf(toReplace, fromIndex)) != -1) {
            buffer.append(input.substring(fromIndex, index) + replaceWith);
            fromIndex = index + 1;
        }

        if (fromIndex < input.length()) {
            buffer.append(input.substring(fromIndex));
        }

        return buffer.toString();
    }

    public static String replaceAll(String input, String toReplace,
                                    String replaceWith) {
        if ((input == null) || (input.length() == 0) || (toReplace == null)
                || (replaceWith == null)) {
            return input;
        }

        int fromIndex = 0;
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        int toReplaceLength = toReplace.length();

        while ((index = input.indexOf(toReplace, fromIndex)) != -1) {
            buffer.append(input.substring(fromIndex, index) + replaceWith);
            fromIndex = index + toReplaceLength;
        }

        if (fromIndex < input.length()) {
            buffer.append(input.substring(fromIndex));
        }

        return buffer.toString();
    }

    public static String replaceAll(String input, String toReplace,
                                    List<String> replaceWith) {

        int nReplaceWith = (replaceWith != null) ? replaceWith.size() : 0;
        if (nReplaceWith == 0)
            return input;

        StringBuffer buffer = new StringBuffer(512);
        int fromIndex = 0;
        int index = -1;
        int toReplaceLength = toReplace.length();
        String s;
        int i = 0;
        while ((index = input.indexOf(toReplace, fromIndex)) != -1) {
            if (i < nReplaceWith) {
                s = replaceWith.get(i);
                i++;
            } else
                s = "";
            buffer.append(input.substring(fromIndex, index) + s);
            fromIndex = index + toReplaceLength;
        }
        if (fromIndex < input.length()) {
            buffer.append(input.substring(fromIndex));
        }
        return buffer.toString();
    }

    public static String truncate(String input, int maxLength) {
        if (input == null || input.trim().length() == 0) {
            return input;
        }

        input = input.trim();
        int length = input.length();

        if (length <= maxLength) {
            return input;
        }

        input = input.substring(0, maxLength);
        int index = input.lastIndexOf(" ");

        if (index != -1) {
            input = input.substring(0, index);
        }

        return input;
    }

    public static boolean isAlphanumeric(String value) {
        return m_alphaNumericMatcher.match(REGEX_ALPHANUMERIC, value);
    }

    /**
     * Check if the string is alpha and numeric
     *
     * @param value
     * @return true of the value is alpha numeric.
     */
    public static boolean isAlphaAndNumeric(String value) {
        if (isAlphanumeric(value)) {
            if (value.matches("(.*)[0-9]+(.*)")
                    && value.matches("(.*)[a-z]+(.*)")) {
                return true;
            }
            if (value.matches("(.*)[0-9]+(.*)")
                    && value.matches("(.*)[A-Z]+(.*)")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumeric(String value) {
        return m_numericMatcher.match(REGEX_NUMERIC, value);
    }

    public static boolean notIncludeWhiteSpaces(String value) {
        return m_withoutwhitespaceMatcher
                .match(REGEX_WITHOUT_WHITESPACE, value);
    }

    public static boolean isAlphanumericOrSpace(String value) {
        return m_alphaNumericSpaceMatcher
                .match(REGEX_SPACE_ALPHANUMERIC, value);
    }

    public static boolean isBarcode(String value) {
        return m_barcodeMatcher.match(REGEX_BARCODE, value);
    }

    public static boolean isMMDDYYYYDate(String value) {
        return m_dateMatcher.match(DATE_REGEXP, value);
    }

    public static boolean isMMDDYYYHHMMSS(String value) {
        return m_dateMatcher.match(TIMESTAMP_REGEXP, value);
    }

    public static boolean isDOBDate(String value) {
        return m_dateMatcher.match(DATE_DOB, value);
    }

    public static boolean isMMDDDate(String value) {
        return m_dateMatcher.match(MM_DD, value);
    }

    public static boolean isMMDDDate2(String value) {
        return m_dateMatcher.match(MM_DD2, value);
    }

    public static boolean isInteger(String value) {

        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isPositiveInteger(String value) {
        int i = 0;

        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return i > 0;

    }

    public static boolean isPositiveIntegerGreaterThanEqualToZero(String value) {
        int i = 0;

        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return i >= 0;

    }

    public static boolean isPositiveLong(String value) {
        long i = 0;

        try {
            i = Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return i > 0;

    }

    public static boolean isDouble(String value) {

        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isFloat(String value) {

        try {
            Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String generateRandomNumeric(int length) {
        int num = 0;
        StringBuffer value = new StringBuffer("");

        for (int i = 0; i < length; i++) {
            num = (new Double(Math.random() * 10)).intValue();
            value.append(num);
        }

        return value.toString();
    }

    public static String generateRandomNumeric(String prefix, int length) {
        return prefix + generateRandomNumeric(length);
    }

    public static boolean isUsPhone(String value) {
        return m_usPhoneMatcher.match(REGEX_US_PHONE, value);
    }

    public static boolean isPhone(String value) {
        return m_usPhoneMatcher.match(REGEX_PHONE, value);
    }

    public static boolean isEmail(String value) {
        return m_emailMatcher.match(REGEX_EMAIL, value);
    }

    public static void showTrace(String msg) {
        try {
            throw new Exception(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String maskText(String text, int to, char ch) {
        return maskText(text, 0, to, ch);
    }

    public static String maskText(String text, int to) {
        return maskText(text, 0, to, 'X');
    }

    public static String maskText(String text, int from, int to, char ch) {
        int textLen = (text != null) ? text.length() : 0;
        StringBuffer maskedText = new StringBuffer(textLen);
        if (from >= 0 && from < to) {
            maskedText.append(text.substring(0, from));
            for (int i = from; i < to; i++) {
                maskedText.append(ch);
            }
            maskedText.append(text.substring(to));
        }
        return maskedText.toString();
    }

    /**
     * Suppress any sensitive attributes for specified xml string.
     *
     * @param xml
     *            the xml string
     * @param attrs
     *            String array of attributes that need to be suppressed, the
     *            values will be replaced as "SUPPRESSED"
     * @return modified xml string
     */
    public static String suppressAttributes(String xml, String[] attrs) {
        StringBuilder aStr = new StringBuilder("(");
        boolean first = true;
        for (String attr : attrs) {
            if (first)
                first = false;
            else
                aStr.append("|");
            aStr.append(attr);

        }
        aStr.append(")(=\")([^\"]*)");
        Pattern p = Pattern.compile(aStr.toString());
        Matcher m = p.matcher(xml);

        StringBuffer result = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(result, m.group(1) + m.group(2) + "SUPPRESSED");

        }
        m.appendTail(result);

        return result.toString();
    }

    public static String trim(String in) {
        if (in == null) {
            return "";
        }
        return in.trim();
    }

    public static List<String> splitToList(String text, String delim,
                                           boolean caseSensitive) {
        List<String> lList = new ArrayList<String>();
        int liPos = 0;
        int liPrevPos = 0;
        String lsString = noNull(text);
        String lsDelim = noNull(delim);
        int liLen = lsDelim.length();

        if (lsString.equals("") || lsDelim.equals("")) {
            lList.add(text);
            return lList;
        }

        if (!caseSensitive) {
            lsString = lsString.toLowerCase();
            lsDelim = lsDelim.toLowerCase();
        }

        liPrevPos = 0;
        liPos = lsString.indexOf(lsDelim);
        while (liPos >= 0) {
            if (liPos == 0) {
                lList.add("");
            } else {
                lList.add(text.substring(liPrevPos, liPos));
            }
            liPrevPos = liPos + liLen;
            liPos = lsString.indexOf(lsDelim, liPrevPos);
        }

        if (liPrevPos > 0) {
            lList.add(text.substring(liPrevPos));
        }

        if (lList.size() == 0) {
            lList.add(text);
        }

        return lList;
    }

    public static String noNull(String text) {
        return (((text == null)) ? "" : text);
    }

    public static String noNull(int val) {
        return (((val == 0)) ? "" : Integer.toString(val));
    }

    public static String noNull(long val) {
        return (((new Long(val)).intValue() == 0) ? "" : Long.toString(val));
    }

    public static String noNull(Long val) {
        return (((val == null || val.intValue() == 0)) ? "" : val.toString());
    }

    public static String noNull(double val) {
        return ((new Double(val).intValue() == 0) ? "" : Double.toString(val));
    }

    public static String noNull(char val) {
        return ((new Character(val).charValue() == 0) ? "" : Character
                .toString(val));
    }

    public static int getTokenCount(String value, String subValue) {
        int count = 0, position = 0, index = 0;
        if (isNotNullNotEmpty(value) && isNotNullNotEmpty(subValue)) {
            while ((index = value.indexOf(subValue, position)) != -1) {
                ++count;
                position = index + subValue.length();
            }
        }
        return count;
    }

    public static String removeNumeric(String text) {
        int size = text.length();
        String str;
        StringBuffer strBuff = new StringBuffer();
        for (int i = 1; i <= size; i++) {
            str = text.substring(i - 1, i);
            if (!isNumeric(str)) {
                strBuff.append(str);
            }
        }
        return strBuff.toString();
    }

    /**
     * @param value
     * @return true if the string matches the pattern
     */
    public static boolean isMMDDYYDate(String value) {

        return m_dateMatcher.match(DATE_REGEXP1, value);
    }

    /**
     * Check the date
     *
     * @param value
     * @return if the pattern is mm/dd/yy return true. otherwise return false
     */
    public static boolean isMMSlashDDSlashYYDate(String value) {
        return m_dateMatcher.match(DATE_REGEXP_SLASHDATE, value);
    }

    /**
     * @param value
     * @return boolean
     */
    public static boolean isCCYY(String value) {
        return m_dateMatcher.match(DATE_CCYY_REGEXP, value);
    }

    /**
     * parse parameter string
     *
     * @param paramString
     *            The Parameter string
     * @return A HashMap of parameters.
     */
    public static HashMap<String, String> parseParamString(String paramString) {
        return parseParamString(paramString, "&", "=");
    }

    /**
     * parse parameter string
     *
     * @param paramString
     *            The Parameter string
     * @param paramPairDelim
     *            The delimeter of the parameter pair
     * @param paramDelim
     *            The delimeter of the parameter
     * @return A HashMap of parameters.
     */
    public static HashMap<String, String> parseParamString(String paramString,
                                                           String paramPairDelim, String paramDelim) {

        HashMap<String, String> map = new HashMap<String, String>();
        if (paramString != null && paramString.length() > 0
                && paramPairDelim != null && paramPairDelim.length() > 0
                && paramDelim != null && paramDelim.length() > 0) {

            StringTokenizer tokenizer = new StringTokenizer(paramString,
                    paramPairDelim);
            String token, name, value;
            int paramDelimLength = paramDelim.length();
            int pos = 0;

            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                if (token != null && token.length() > 0
                        && (pos = token.indexOf(paramDelim)) != -1) {
                    name = token.substring(0, pos);
                    value = token.substring(pos + paramDelimLength);
                    if (name != null && value != null
                            && (name = name.trim()).length() > 0) {
                        value = value.trim();
                        map.put(name.trim(), value.trim());
                    }
                }
            }
        }
        return map;
    }

    public static boolean toBoolean(String text, boolean defaultValue) {
        boolean value = defaultValue;
        if (text != null && text.length() > 0) {
            try {
                Boolean b = new Boolean(text);
                value = b.booleanValue();
            } catch (Throwable e) {
            }
        }
        return value;
    }

    public static float toFloat(String text, float defaultValue) {
        float value = defaultValue;
        if (text != null && text.length() > 0) {
            try {
                value = Float.parseFloat(text);
            } catch (Throwable e) {
            }
        }
        return value;
    }

    public static double toDouble(String text, double defaultValue) {
        double value = defaultValue;
        if (text != null && text.length() > 0) {
            try {
                value = Double.parseDouble(text);
            } catch (Throwable e) {
            }
        }
        return value;
    }

    public static long toLong(String text, long defaultValue) {
        long value = defaultValue;
        if (text != null && text.length() > 0) {
            try {
                value = Long.parseLong(text);
            } catch (Throwable e) {
            }
        }
        return value;
    }

    public static char toChar(String text, char defaultChar) {
        char c = defaultChar;
        String trimText;
        if (text != null && (trimText = text.trim()).length() > 0)
            c = trimText.charAt(0);
        return c;
    }

    public static boolean isNotNullNotEmpty(String text) {
        return isNotNullNotEmpty(text, false);
    }

    public static boolean isNotNullNotEmpty(String text, boolean trim) {
        if (trim) {
            if (text != null && text.trim().length() > 0) {
                return true;
            }
        } else {
            if (text != null && text.length() > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String attribute, String text) {
        int compileOptions1 = Perl5Compiler.DEFAULT_MASK
                | Perl5Compiler.READ_ONLY_MASK;
        PatternCache patternCache1 = new PatternCacheLRU(new Perl5Compiler());
        String regExp = "/.*" + text + ".*/";
        patternCache1.getPattern(regExp, compileOptions1);
        Perl5Util stringMatcher = new Perl5Util(patternCache1);
        return stringMatcher.match(regExp, attribute);
    }

    public static String decodeXML(String text) {
        String returnString = replaceAll(text, "&amp;", "&");
        returnString = replaceAll(returnString, "&apos;", "'");
        returnString = replaceAll(returnString, "&quot;", "\"");
        returnString = replaceAll(returnString, "&lt;", "<");
        returnString = replaceAll(returnString, "&gt;", ">");
        return returnString;
    }

    /**
     * convert MM-DD to MM/DD
     *
     * @param inputStr
     * @return converted string
     */
    public static String convertToMMDDPattern(String inputStr) {
        String returnStr = null;
        if (!isNotNullNotEmpty(inputStr))
            returnStr = "";
        else if (isMMDDDate(inputStr) && inputStr.length() == 5)
            returnStr = inputStr;
        else {
            StringBuffer sb = new StringBuffer();
            returnStr = replaceAll(inputStr, '-', "/");
            String temp[] = returnStr.split("/");
            if (temp.length != 2)
                returnStr = "";
            else {
                String single = (temp[0].length() == 1 ? "0" + temp[0]
                        : temp[0]);
                sb.append(single);
                sb.append("/");
                single = (temp[1].length() == 1 ? "0" + temp[1] : temp[1]);
                sb.append(single);
                returnStr = sb.toString();
            }
        }

        return returnStr;
    }

    /**
     * Method will convert list into String by appending the separator
     *
     * @param list
     *            - List
     * @param elementSeparator
     *            - Separator to be used
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String listToString(List<Object> list, String elementSeparator) {
        return listToString(list, elementSeparator, "", "");
    }

    /**
     * Method will convert list into String by appending the Separator
     *
     * @param list
     *            - List
     * @param elementSeparator
     *            - Separator to be used between element
     * @param elementEnclosure
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String listToString(List<Object> list,
                                      String elementSeparator, String elementEnclosure) {
        return listToString(list, elementSeparator, elementEnclosure,
                elementEnclosure);
    }

    /**
     * Method will convert list into String by appending the Separator
     *
     * @param list
     *            - List
     * @param elementSeparator
     *            - Deliminator to be used
     * @param elementPrefix
     *            - Optional Prefix surrounding the element
     * @param elementSuffix
     *            - Optional Suffix surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String listToString(List<Object> list,
                                      String elementSeparator, String elementPrefix, String elementSuffix) {
        StringBuffer buffer = new StringBuffer();
        Iterator<Object> iterator = (list != null) ? list.iterator() : null;
        if (iterator != null) {
            Object element;
            elementSeparator = (elementSeparator == null ? ""
                    : elementSeparator);
            elementPrefix = (elementPrefix == null ? "" : elementPrefix);
            elementSuffix = (elementSuffix == null ? "" : elementSuffix);
            boolean first = true;
            while (iterator.hasNext()) {
                element = iterator.next();
                if (element != null) {
                    if (first) {
                        first = false;
                    } else {
                        buffer.append(elementSeparator);
                    }
                    buffer.append(elementPrefix).append(element.toString())
                            .append(elementSuffix);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * This utility method is used to split the result string into multiple
     * strings if result string is larger than the maximum length limit.
     *
     * If maximum length limit value is smaller than the length of the result
     * string plus a single element, an IllegalArgumentException would be thrown
     * out. <br>
     * <br>
     * Here is an example of the output called with below input:<br>
     * Object[] list = new String[] {"A", "B", "C", "D", "E", "F", "G", "H",
     * "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R"};<br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 42));<br>
     * Result: Exception encountered.</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 43));<br>
     * Result: {0|1=SELECT ID, NAME FROM ITEM WHERE ID IN('A');, 1|2=SELECT ID,
     * NAME FROM ITEM WHERE ID IN('B');, 2|3=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('C');, 3|4=SELECT ID, NAME FROM ITEM WHERE ID IN('D');, 4|5=SELECT ID,
     * NAME FROM ITEM WHERE ID IN('E');, 5|6=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('F');, 6|7=SELECT ID, NAME FROM ITEM WHERE ID IN('G');, 7|8=SELECT ID,
     * NAME FROM ITEM WHERE ID IN('H');, 8|9=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('I');, 9|10=SELECT ID, NAME FROM ITEM WHERE ID IN('J');, 10|11=SELECT
     * ID, NAME FROM ITEM WHERE ID IN('K');, 11|12=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('L');, 12|13=SELECT ID, NAME FROM ITEM WHERE ID IN('M');,
     * 13|14=SELECT ID, NAME FROM ITEM WHERE ID IN('N');, 14|15=SELECT ID, NAME
     * FROM ITEM WHERE ID IN('O');, 15|16=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('P');, 16|17=SELECT ID, NAME FROM ITEM WHERE ID IN('Q');, 17|18=SELECT
     * ID, NAME FROM ITEM WHERE ID IN('R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 47));<br>
     * Result: {0|2=SELECT ID, NAME FROM ITEM WHERE ID IN('A','B');, 2|4=SELECT
     * ID, NAME FROM ITEM WHERE ID IN('C','D');, 4|6=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('E','F');, 6|8=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('G','H');, 8|10=SELECT ID, NAME FROM ITEM WHERE ID IN('I','J');,
     * 10|12=SELECT ID, NAME FROM ITEM WHERE ID IN('K','L');, 12|14=SELECT ID,
     * NAME FROM ITEM WHERE ID IN('M','N');, 14|16=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('O','P');, 16|18=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 51));<br>
     * Result: {0|3=SELECT ID, NAME FROM ITEM WHERE ID IN('A','B','C');,
     * 3|6=SELECT ID, NAME FROM ITEM WHERE ID IN('D','E','F');, 6|9=SELECT ID,
     * NAME FROM ITEM WHERE ID IN('G','H','I');, 9|12=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('J','K','L');, 12|15=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('M','N','O');, 15|18=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('P','Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 55));<br>
     * Result: {0|4=SELECT ID, NAME FROM ITEM WHERE ID IN('A','B','C','D');,
     * 4|8=SELECT ID, NAME FROM ITEM WHERE ID IN('E','F','G','H');, 8|12=SELECT
     * ID, NAME FROM ITEM WHERE ID IN('I','J','K','L');, 12|16=SELECT ID, NAME
     * FROM ITEM WHERE ID IN('M','N','O','P');, 16|18=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 59));<br>
     * Result: {0|5=SELECT ID, NAME FROM ITEM WHERE ID IN('A','B','C','D','E');,
     * 5|10=SELECT ID, NAME FROM ITEM WHERE ID IN('F','G','H','I','J');,
     * 10|15=SELECT ID, NAME FROM ITEM WHERE ID IN('K','L','M','N','O');,
     * 15|18=SELECT ID, NAME FROM ITEM WHERE ID IN('P','Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 63));<br>
     * Result: {0|6=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('A','B','C','D','E','F');, 6|12=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('G','H','I','J','K','L');, 12|18=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('M','N','O','P','Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 67));<br>
     * Result: {0|7=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('A','B','C','D','E','F','G');, 7|14=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('H','I','J','K','L','M','N');, 14|18=SELECT ID, NAME FROM ITEM WHERE
     * ID IN('O','P','Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 75));<br>
     * Result: {0|9=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('A','B','C','D','E','F','G','H','I');, 9|18=SELECT ID, NAME FROM ITEM
     * WHERE ID IN('J','K','L','M','N','O','P','Q','R');}</li> <br>
     * <br>
     * <li>System.out.println(listToString(Arrays.asList(list),
     * "SELECT ID, NAME FROM ITEM WHERE ID IN(", ");", ",", "'", "'", 111));<br>
     * Result: {0|18=SELECT ID, NAME FROM ITEM WHERE ID
     * IN('A','B','C','D','E','F'
     * ,'G','H','I','J','K','L','M','N','O','P','Q','R');}</li> <br>
     * <br>
     *
     * @param <T>
     *            the generic type
     * @param list
     *            the list
     * @param resultPrefix
     *            the result prefix
     * @param resultSuffix
     *            the result suffix
     * @param elementSeparator
     *            the element separator
     * @param elementPrefix
     *            the element prefix
     * @param elementSuffix
     *            the element suffix
     * @param maximumLengthLimit
     *            the maximum length limit
     * @return the list
     * @since 7.20.000
     */
    public static <T> LinkedHashMap<String, String> listToString(List<T> list,
                                                                 String resultPrefix, String resultSuffix, String elementSeparator,
                                                                 String elementPrefix, String elementSuffix, int maximumLengthLimit) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        StringBuffer buffer = null;
        if (list != null && list.size() > 0) {
            T element;
            resultPrefix = (resultPrefix == null ? "" : resultPrefix);
            resultSuffix = (resultSuffix == null ? "" : resultSuffix);
            elementSeparator = (elementSeparator == null ? ""
                    : elementSeparator);
            elementPrefix = (elementPrefix == null ? "" : elementPrefix);
            elementSuffix = (elementSuffix == null ? "" : elementSuffix);

            int resultSuffixLength = resultSuffix.length();
            int resultPrefixAndSuffixLength = resultPrefix.length()
                    + resultSuffixLength;
            int elementSeparatorLength = elementSeparator.length();
            int elementPrefixAndSuffixLength = elementPrefix.length()
                    + elementSuffix.length();
            int startIndex = 0, previousStartIndex = 0;

            for (int i = 0; i < list.size();) {
                boolean first = true;
                buffer = new StringBuffer(resultPrefix);

                while (i < list.size()) {
                    element = list.get(i);
                    if (element != null) {
                        if (first) {
                            if ((resultPrefixAndSuffixLength
                                    + element.toString().length() + elementPrefixAndSuffixLength) > maximumLengthLimit) {
                                throw new IllegalArgumentException(
                                        "Maximum result length limit is smaller than addition result of prefix, suffix and a single element length in the list. Please increase the value of maximumLengthLimit. maximumLengthLimit="
                                                + maximumLengthLimit
                                                + ";currentElement="
                                                + element.toString());
                            } else {
                                first = false;
                                buffer.append(elementPrefix)
                                        .append(element.toString())
                                        .append(elementSuffix);
                            }
                        } else {
                            if ((buffer.length() + elementSeparatorLength
                                    + element.toString().length()
                                    + resultSuffixLength + elementPrefixAndSuffixLength) <= maximumLengthLimit) {
                                buffer.append(elementSeparator);
                                buffer.append(elementPrefix)
                                        .append(element.toString())
                                        .append(elementSuffix);
                            } else {
                                break;
                            }
                        }
                    }
                    i++;
                }
                buffer.append(resultSuffix);

                previousStartIndex = startIndex;
                startIndex = i;
                result.put(previousStartIndex + "|" + startIndex,
                        buffer.toString());
            }
        }
        return result;
    }

    /**
     * Method will convert int array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(int[] array, String elementSeparator) {
        return ArrayToString(array, elementSeparator, "", "");
    }

    /**
     * Method will convert int array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Seperator to be used
     * @param elementEnclosure
     *            - String surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(int[] array, String elementSeparator,
                                       String elementEnclosure) {
        return ArrayToString(array, elementSeparator, elementEnclosure,
                elementEnclosure);
    }

    /**
     * Method will convert int array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @param elementPrefix
     *            - Optional Prefix surrounding the element
     * @param elementSuffix
     *            - Optional Suffix surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(int[] array, String elementSeparator,
                                       String elementPrefix, String elementSuffix) {
        StringBuffer buffer = new StringBuffer();
        if (array != null) {
            elementSeparator = (elementSeparator == null ? ""
                    : elementSeparator);
            elementPrefix = (elementPrefix == null ? "" : elementPrefix);
            elementSuffix = (elementSuffix == null ? "" : elementSuffix);
            boolean first = true;
            for (int i = 0; i < array.length; i++) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(elementSeparator);
                }
                buffer.append(elementPrefix).append(array[i])
                        .append(elementSuffix);
            }
        }
        return buffer.toString();
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(long[] array, String elementSeparator) {
        return ArrayToString(array, elementSeparator, "", "");
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Seperator to be used
     * @param elementEnclosure
     *            - String surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(long[] array, String elementSeparator,
                                       String elementEnclosure) {
        return ArrayToString(array, elementSeparator, elementEnclosure,
                elementEnclosure);
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @param elementPrefix
     *            - Optional Prefix surrounding the element
     * @param elementSuffix
     *            - Optional Suffix surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(long[] array, String elementSeparator,
                                       String elementPrefix, String elementSuffix) {
        StringBuffer buffer = new StringBuffer();
        if (array != null) {
            elementSeparator = (elementSeparator == null ? ""
                    : elementSeparator);
            elementPrefix = (elementPrefix == null ? "" : elementPrefix);
            elementSuffix = (elementSuffix == null ? "" : elementSuffix);
            boolean first = true;
            for (int i = 0; i < array.length; i++) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(elementSeparator);
                }
                buffer.append(elementPrefix).append(array[i])
                        .append(elementSuffix);
            }
        }
        return buffer.toString();
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(Object[] array, String elementSeparator) {
        return ArrayToString(array, elementSeparator, "", "");
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Seperator to be used
     * @param elementEnclosure
     *            - String surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(Object[] array, String elementSeparator,
                                       String elementEnclosure) {
        return ArrayToString(array, elementSeparator, elementEnclosure,
                elementEnclosure);
    }

    /**
     * Method will convert long array into String by appending the Separator
     *
     * @param array
     * @param elementSeparator
     *            - Deliminator to be used
     * @param elementPrefix
     *            - Optional Prefix surrounding the element
     * @param elementSuffix
     *            - Optional Suffix surrounding the element
     * @return - String representation of List by calling toString() method on
     *         all objects in list. e.g return something like 1,2,3,4 etc.
     */
    public static String ArrayToString(Object[] array, String elementSeparator,
                                       String elementPrefix, String elementSuffix) {
        StringBuffer buffer = new StringBuffer();
        if (array != null) {
            elementSeparator = (elementSeparator == null ? ""
                    : elementSeparator);
            elementPrefix = (elementPrefix == null ? "" : elementPrefix);
            elementSuffix = (elementSuffix == null ? "" : elementSuffix);
            boolean first = true;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    if (first) {
                        first = false;
                    } else {
                        buffer.append(elementSeparator);
                    }
                    buffer.append(elementPrefix).append(array[i].toString())
                            .append(elementSuffix);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * @param text
     * @param defaultValue
     * @return int value
     */
    public static int toInteger(String text, int defaultValue) {
        int value = defaultValue;
        if (text != null && text.length() > 0) {
            try {
                value = Integer.parseInt(text);
            } catch (Throwable e) {
            }
        }
        return value;
    }

    /**
     * Convert int to array of bytes with length 4
     *
     * @param intValue
     * @return An array of bytes with length 4
     */
    public static byte[] intToByteArray(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (4 - 1 - i) * 8;
            b[i] = (byte) ((intValue >>> offset) & 0xFF);
        }
        return b;
    }

    public static String appendWithSeperator(String text1, String text2,
                                             String seperator) {
        // Return text1 + seperator + text2 if both text1 & text2 are
        // not null and empty
        // Othersize return text1 or text2 depending on which one is not
        // null and empty
        int size1 = (text1 == null) ? 0 : text1.length();
        int size2 = (text2 == null) ? 0 : text2.length();
        if (size1 > 0 && size2 > 0)
            return text1.concat(seperator).concat(text2);
        else if (size1 > 0)
            return text1;
        else
            return text2;
    }

    /**
     * @param xml
     * @return string after special chars removed
     */
    public static String removeSpecialCharacters(String xml) {
        xml = replaceAll(xml, "&amp;apos;", "&#38;apos;");
        xml = replaceAll(xml, "&amp;", "&#38;amp;");
        xml = replaceAll(xml, "&lt;", "&#38;lt;");
        xml = replaceAll(xml, "&gt;", "&#38;gt;");
        return xml;
    }

    /**
     * @param xml
     * @return string after special chars removed
     */
    public static String removeVerySpecialCharacters(String xml) {
        xml = replaceAll(xml, "&#10;", "%lfTempChangealf%");
        xml = replaceAll(xml, "&", "%ampTempChangeamp%");
        xml = replaceAll(xml, "<", "%ltTempChangelt%");
        xml = replaceAll(xml, ">", "%gtTempChangegt%");
        xml = replaceAll(xml, "'", "%aposTempChangeapos%");
        xml = replaceAll(xml, "&amp;apos;", "&#38;apos;");
        xml = replaceAll(xml, "&amp;", "&#38;amp;");
        xml = replaceAll(xml, "&lt;", "&#38;lt;");
        xml = replaceAll(xml, "&gt;", "&#38;gt;");
        xml = replaceAll(xml, "%ampTempChangeamp%", "&amp;");
        xml = replaceAll(xml, "%ltTempChangelt%", "&lt;");
        xml = replaceAll(xml, "%gtTempChangegt%", "&gt;");
        xml = replaceAll(xml, "%aposTempChangeapos%", "&apos;");
        xml = replaceAll(xml, "%lfTempChangealf%", "&#10;");
        return xml;
    }

    /**
     * Convert contained '&' to xml format '&#38;' for a xml string
     *
     * @param xml
     *            original xml string
     * @return xml string
     */
    public static String convertCharAndToXmlFormat(String xml) {
        return replaceAll(xml, "&", XML_CHAR_AND);
    }

    /**
     * Replace strings using the given string matrix. The format of the matrix is like {{target, replacement},{target, replacement}...}.
     *
     * @param text
     * @param replpaceMatrix
     *                 the string replacement matrix. For example {{ "&lt;", " <" }, {
     *                 "&gt;", ">" } } means replace all the "&lt; with " <" and all
     *                 the "&gt;" with">".
     * @return replaced string
     */
    public static String batchReplace(String text, String[][] replpaceMatrix) {
        if (text == null || replpaceMatrix == null) {
            return text;
        }

        StringBuffer sbPattern = new StringBuffer();
        boolean beginning = true;
        for (int i = 0; i < replpaceMatrix.length; i++) {
            if (!beginning) {
                sbPattern.append("|");
            }
            sbPattern.append("(").append(replpaceMatrix[i][0]).append(")");
            beginning = false;
        }
        Pattern p = Pattern.compile(sbPattern.toString());

        Matcher m = p.matcher(text);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
            for (int i = 0; i < replpaceMatrix.length; i++) {
                if (m.group(i + 1) != null) {
                    m.appendReplacement(result, replpaceMatrix[i][1]);
                    break;
                }
            }
        }
        m.appendTail(result);
        return result.toString();
    }

    /**
     * To compare two specifc String Objects, If they are both null then believe
     * they are equal
     *
     * @param text1
     * @param text2
     * @param isIgnoreCase
     *            : true, to compare in ignore case way
     * @return true if the two strings are equal
     */
    public static boolean isEqualString(String text1, String text2,
                                        boolean isIgnoreCase) {
        return (text1 == null && text2 == null)
                || (text1 != null && text2 != null && (isIgnoreCase ? text1
                .equalsIgnoreCase(text2) : text1.equals(text2)));
    }

    /**
     * Convert UTC/GMT datetime string in ISO-8601 format to local datetime of
     * java.util.Date.
     *
     * @param date
     * @return java.util.Date
     * @throws ParseException
     */
    public static Date convertUTCStringToDate(String date)
            throws ParseException {
        return ISO_DATETIME_FORMAT.parse(date);
    }

    /**
     * Convert MM/dd/yyyy format string to local datetime of java.util.Date.
     *
     * @param date
     * @return java.util.Date
     * @throws ParseException
     */
    public static Date convertMMDDYYYYStringToDateTime(String date)
            throws ParseException {
        return DATETIME_FORMAT_SHORT.parse(date);
    }

    /**
     * Convert MM/dd/yyyy format string to local datetime of java.util.Date.
     *
     * @param date
     * @return java.util.Date
     * @throws ParseException
     */
    public static Date convertMMDDYYYYStringToDate(String date)
            throws ParseException {
        return DATE_FORMAT_SHORT.parse(date);
    }

    /**
     * Convert h:mm a format string to local datetime of java.util.Date.
     *
     * @param date
     * @return java.util.Date
     * @throws ParseException
     */
    public static Date convertShortTimeStringToDate(String date)
            throws ParseException {
        return TIME_FORMAT_SHORT.parse(date);
    }

    /**
     * Convert "HH:mm" format string to local DateTime of java.util.Date.
     *
     * @param date
     * @return converted date
     * @throws ParseException
     */
    public static Date convert24HourStringToDate(String date)
            throws ParseException {
        return TIME_FORMAT_24_HOUR.parse(date);
    }

    /**
     * Convert date/time to "HH:mm" format string.
     *
     * @param time
     * @return converted string
     */
    public static String convertTimeTo24HourString(Date time) {
        return TIME_FORMAT_24_HOUR.format(time);
    }

    /**
     * Convert local datetime of java.util.Date to UTC/GMT datetime in ISO-8601
     * format
     *
     * @param date
     * @return converted string
     */
    public static String convertDateToUTCString(Date date) {
        return ISO_DATETIME_FORMAT.format(date);
    }

    /**
     * Convert local datetime of java.util.Date to MM/dd/yyyy format
     *
     * @param date
     * @return converted string
     */
    public static String convertDateMMDDYYYYString(Date date) {
        return DATE_FORMAT_SHORT.format(date);
    }

    /**
     * Convert local datetime of java.util.Date to MM/dd/yyyy format
     *
     * @param date
     * @return converted string
     */
    public static String convertDateTimeMMDDYYYYString(Date date) {
        return DATETIME_FORMAT_SHORT.format(date);
    }

    /**
     * Convert local datetime of java.util.Date to h:mm a format
     *
     * @param date
     * @return converted string
     */
    public static String convertShortTimeString(Date date) {
        return TIME_FORMAT_SHORT.format(date);
    }

    /**
     * Convert java.util.Double to decimal with specified digits
     *
     * @param data
     * @param decimalDigits
     * @return converted string
     */
    public static String convertDoubleToDecimalString(Double data,
                                                      int decimalDigits) {
        StringBuffer pattern = new StringBuffer(32);
        pattern.append("###");
        for (int i = 0; i < decimalDigits; i++) {
            if (i == 0) {
                pattern.append(".");
            }
            pattern.append("#");
        }
        DecimalFormat formatter = new DecimalFormat(pattern.toString());
        return formatter.format(data);
    }

    /**
     * Returns the first character of a string.
     *
     * @param str
     * @return char - If str is null returns 0, else returns the first character
     *         of the str
     */
    /*public static char charAtZero(String str) {
        char c = PsiConstants.CHAR_NULL;
        if (str != null) {
            c = str.charAt(0);
        }
        return c;
    }*/

    /**
     * Even java.util.Date doesn't contains any timezone information, and the
     * time millisecond is always a UTC based time, however, when we store the
     * date into date base with below statement:
     *
     * statement.setTimestamp(1, new Timestamp(System.currentMillis()))
     *
     * The time information with the timezone information would be stored into
     * database even the timezone setting of the database is GMT. To correct
     * this behaviour, we provide this method to substract the correponding
     * timezone information of the date from the original date and returned a
     * new equavlent date of the GMT timezone.
     *
     * For instance, with an original date of PST date "2012-03-21 00:33:11.87",
     * the result output date would be "2012-03-21 08:33:11.87"
     *
     * @param date
     *            the date
     * @return the date
     */
    public static Date stripTimeZoneOffset(Date date) {
        Date result = null;
        if (date != null) {
            try {
                result = DATETIME_FORMAT_LONG.parse(GMT_DATETIME_FORMAT_LONG
                        .format(date));
            } catch (ParseException e) {
            }
        }

        return result;
    }

    /**
     * Strip time zone offset from a java.util.Date that the input time
     * millisecond stands for.
     *
     * @param timeMillis
     *            the time millis
     * @return the date
     */
    public static long stripTimeZoneOffset(long timeMillis) {
        Date result = stripTimeZoneOffset(new Date(timeMillis));
        return result == null ? 0L : result.getTime();
    }

    /**
     * To camel string.
     *
     * @param text
     *            the text
     * @return the string
     */
    public static String toCamelString(String text) {
        return toCamelString(text, "_");
    }

    /**
     * To camel string.
     *
     * @param text
     *            the text
     * @param delimiter
     *            the delimiter
     * @return the string
     */
    public static String toCamelString(String text, String delimiter) {
        if (text == null) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer();
            if (delimiter != null) {
                for (String s : text.split(delimiter)) {
                    if (isNotNullNotEmpty(s)) {
                        buffer.append(s.substring(0, 1).toUpperCase());

                        if (s.length() > 1) {
                            buffer.append(s.substring(1).toLowerCase());
                        }
                    }
                }
            } else {
                buffer.append(text);
            }
            return buffer.toString();
        }
    }

    /**
     * @param xml
     * @return encoded xml string
     */
    public static String encodeXML(String xml) {
        xml = replaceAll(xml, "&", "&amp;");
        xml = replaceAll(xml, "<", "&lt;");
        xml = replaceAll(xml, ">", "&gt;");
        xml = replaceAll(xml, "'", "&apos;");
        xml = replaceAll(xml, "\"", "&quot;");
        return xml;
    }

    public static boolean startWithWildcardCharacters(String value) {
        if (isNotNullNotEmpty(value)) {
            for (String wc : wildcardchars) {
                if (value.startsWith(wc)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns only numeric values from string
     *
     * @param value
     * @return
     */
    public static String getNumericOnly(String value) {
        if (hasValue(value)) {
            return value.trim().replaceAll(REGEX_NUMERIC_ONLY, "");
        } else {
            return value;
        }

    }

    public static String convertUTCDateToStringWithTimezone(Date date,
                                                            TimeZone timezone) {
        ISO_DATETIME_FORMAT_WITH_TZ.setTimeZone(timezone);
        return ISO_DATETIME_FORMAT_WITH_TZ.format(date);
    }

    /**
     * Checks if String number is a malformed number.
     *
     * @param number
     * @return true if number is null, empty, or not numeric, false otherwise.
     *
     * @since 8.0
     */
    public static boolean isMalformedStringNumber(String number) {
        return ((isNotNullNotEmpty(number) && !isNumeric(number)));
    }

    /**
     * Joins the elements of the provided Iterator into a single String
     * containing the provided elements. No delimiter is added before or after
     * the list. Null objects or empty strings within the iteration are
     * represented by empty strings.
     *
     * @param iterator
     * @param separator
     * @return the joined String, null if null iterator input
     */
    public static String join(Iterator iterator, char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first == null ? "" : first.toString();
        }

        // two or more elements
        StringBuffer buf = new StringBuffer(256); // Java default is 16,
        // probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided Collection into a single String
     * containing the provided elements. No delimiter is added before or after
     * the list. Null objects or empty strings within the iteration are
     * represented by empty strings.
     *
     * @param collection
     * @param separator
     * @return the joined String, null if null iterator input
     */
    public static String join(Collection collection, char separator) {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }


    /**
     * Check string to
     * @param value
     * @return
     */
    public static boolean isCamelCase(String value) {
        return value.matches(REGEX_CAMEL_CASE);
    }
}

