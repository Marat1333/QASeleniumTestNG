package com.leroy.core.configuration;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

public class AssertVerification extends EnvironmentConfigurator {

    protected SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void assertVerificationBeforeMethod() {
        softAssert = new SoftAssert();
    }

    @AfterMethod
    public void assertVerificationAfterMethod() {
        softAssert = null;
    }

    public void softAssertTrue(boolean condition, String desc) {
        softAssert.assertTrue(condition, desc);
        if(!condition) {
            Log.assertFail(desc);
        }
    }

    public void softAssertFalse(boolean condition, String desc) {
        softAssert.assertFalse(condition, desc);
        if(condition) {
            Log.assertFail(desc);
        }
    }

    public void softAssertEquals(Object actual, Object expected, String desc) {
        softAssert.assertEquals(actual,	expected, desc);
        if(!actual.equals(expected)) {
            Log.assertFail(desc);
        }
    }

    public void softAssertNotEquals(Object actual, Object expected, String desc) {
        softAssert.assertNotEquals(actual, expected, desc);
        if(actual.equals(expected)) {
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
    public void softAssertContains(String inputString, String searchSubString, String desc) {
        softAssert.assertTrue(inputString.contains(searchSubString), desc + "\nActual string = " + inputString + "\nExpected substring = " + searchSubString + "\n");
        if(!inputString.contains(searchSubString)) {
            Log.assertFail(desc);
        }
    }

    public void softAssertNull(Object object, String desc) {
        softAssert.assertNull(object, desc);
        if(object != null) {
            Log.assertFail(desc);
        }
    }

    public void softAssertNotNull(Object object, String desc) {
        softAssert.assertNotNull(object, desc);
        if(object == null) {
            Log.assertFail(desc);
        }
    }

    public void softAssertNotSame(Object actual, Object expected, String desc) {
        softAssert.assertNotSame(actual, expected, desc);
        if(actual != expected) {
            Log.assertFail(desc);
        }
    }

    public void softAssertAll() {
        softAssert.assertAll();
    }

}

