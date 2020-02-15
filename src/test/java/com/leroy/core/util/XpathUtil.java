package com.leroy.core.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.testng.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class XpathUtil {

    /**
     * Get xpath from By (locator)
     *
     * @return String
     */
    public static String getXpath(By by) {
        String str = by.toString();
        int idx_split = str.indexOf(" ");
        String type = str.substring(0, idx_split);
        switch (type) {
            case "By.xpath:":
                return str.substring(idx_split).trim();
            case "By.id:":
                return "//*[@id='" + str.substring(idx_split).trim() + "']";
        }
        return str;
    }

    public static String getXpathByAccessibilityId(String accessibilityId) {
        return "//*[@content-desc='" + accessibilityId + "']";
    }

    /**
     * Get an xpath by index
     *
     * @param xpath
     * @param index
     * @return String
     */
    public static String getXpathByIndex(String xpath, int index) {
        if (Strings.isNotNullAndNotEmpty(xpath) && index >= 0)
            return "(" + xpath + ")[" + (index + 1) + "]";
        else
            throw new RuntimeException("Method XpathUtil.getXpathByIndex (" + xpath + ", " + index + ") - " +
                    "xpath should be not null and not empty, index should be non-negative");
    }

    private static String getAttributeValueFromXpath(String xpath, String attribute) {
        String value = StringUtils.substringBetween(xpath, attribute + "='", "'");
        if (value == null)
            value = StringUtils.substringBetween(xpath, attribute + "=\"", "\"");
        return value;
    }

    public static UiSelector convertXpathToUISelector(String xpath) {
        //"//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]//android.view.ViewGroup[@content-desc=\"Button\"]"
        //"//android.widget.ProgressBar"
        //"//android.widget.TextView[@text='Все отделы']/following::android.widget.TextView[1]"
        List<String> nodesInXpath = new ArrayList<>();
        StringBuilder oneNodeXpath = new StringBuilder();
        for (int i = 0; i < xpath.length(); i++) {
            char currentChar = xpath.charAt(i);
            if (currentChar == '/') {
                if (oneNodeXpath.length() != 0)
                    nodesInXpath.add(oneNodeXpath.toString());
                oneNodeXpath = new StringBuilder();
                if (xpath.charAt(i+1) == '/') {
                    oneNodeXpath.append("/");
                    i++;
                }
            }
            oneNodeXpath.append(currentChar);
            if ((i+1) == xpath.length())
                nodesInXpath.add(oneNodeXpath.toString());
        }
        UiSelector selector = new UiSelector();
        if (nodesInXpath.size() == 1) {
            String elemXpath = nodesInXpath.get(0);
            if (!elemXpath.contains("*")) {
                String classNodeName = StringUtils.substringBetween(elemXpath, "//", "[");
                if (classNodeName != null)
                    selector.className(classNodeName);
                else {
                    selector.className(StringUtils.substringAfterLast(elemXpath, "/"));
                }
            }
            if (elemXpath.contains("@content-desc="))
                selector.description(getAttributeValueFromXpath(xpath, "content-desc"));
        }
        return selector;
    }
}
