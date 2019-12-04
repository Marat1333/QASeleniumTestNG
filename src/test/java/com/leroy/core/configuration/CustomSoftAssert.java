package com.leroy.core.configuration;

import com.leroy.core.web_elements.general.Element;
import org.testng.asserts.SoftAssert;

public class CustomSoftAssert {

    private SoftAssert softAssert;

    public CustomSoftAssert() {
        softAssert = new SoftAssert();
    }

    public void isTrue(boolean condition, String desc) {
        softAssert.assertTrue(condition, desc);
        if (!condition) {
            Log.assertFail(desc);
        }
    }

    public void isFalse(boolean condition, String desc) {
        softAssert.assertFalse(condition, desc);
        if (condition) {
            Log.assertFail(desc);
        }
    }

    public void isEquals(Object actual, Object expected, String desc) {
        softAssert.assertEquals(actual, expected, desc);
        if (!actual.equals(expected)) {
            Log.assertFail(desc);
        }
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        softAssert.assertNotEquals(actual, expected, desc);
        if (actual.equals(expected)) {
            Log.assertFail(desc);
        }
    }

    /**
     * Soft assert if inputString contains searchSubString
     *
     * @param inputString
     * @param searchSubString
     * @param desc
     */
    public void isContains(String inputString, String searchSubString, String desc) {
        softAssert.assertTrue(inputString.contains(searchSubString),
                desc + "\nActual string = " + inputString + "\nExpected substring = " + searchSubString + "\n");
        if (!inputString.contains(searchSubString)) {
            Log.assertFail(desc);
        }
    }

    public void isNull(Object object, String desc) {
        softAssert.assertNull(object, desc);
        if (object != null) {
            Log.assertFail(desc);
        }
    }

    public void isNotNull(Object object, String desc) {
        softAssert.assertNotNull(object, desc);
        if (object == null) {
            Log.assertFail(desc);
        }
    }

    public void isElementTextEqual(Element element, String expectedText) {
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " is not visible";
        softAssert.assertTrue(elementVisibility, desc);
        if (!elementVisibility)
            Log.assertFail(desc);
        else {
            String actualText = element.getText();
            String desc2 = element.getMetaName() + " has incorrect text";
            softAssert.assertEquals(actualText, expectedText,
                   desc2 );
            if (!actualText.equals(expectedText)) {
                Log.assertFail(desc2);
            }
        }
    }

    public void verifyAll() {
        softAssert.assertAll();
    }

}
