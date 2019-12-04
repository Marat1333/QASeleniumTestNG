package com.leroy.core.util;

import org.openqa.selenium.By;
import org.testng.util.Strings;

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
                return "//*[@id='"+str.substring(idx_split).trim()+"']";
        }
        return str;
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
            throw new RuntimeException("Method XpathUtil.getXpathByIndex (" +  xpath + ", " + index + ") - " +
                    "xpath should be not null and not empty, index should be non-negative");
    }
}
