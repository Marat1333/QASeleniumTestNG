package com.leroy.core.configuration;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.web_elements.general.Element;
import org.testng.asserts.SoftAssert;

public class CustomSoftAssert {

    private SoftAssert softAssert;
    private StepLog stepLog;

    public CustomSoftAssert(StepLog stepLog) {
        softAssert = new SoftAssert();
        this.stepLog = stepLog;
    }

    public void isTrue(boolean condition, String desc) {
        softAssert.assertTrue(condition, desc);
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        if (curStepRes != null)
            curStepRes.setExpected(desc);
        if (!condition) {
            if (curStepRes != null)
                curStepRes.setActual("Opposite to the expected result");
            stepLog.assertFail(desc);
        }
    }

    public void isFalse(boolean condition, String desc) {
        softAssert.assertFalse(condition, desc);
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        if (curStepRes != null)
            curStepRes.setExpected(desc);
        if (condition) {
            if (curStepRes != null)
                curStepRes.setActual("Opposite to the expected result");
            stepLog.assertFail(desc);
        }
    }

    public void isEquals(Object actual, Object expected, String desc) {
        softAssert.assertEquals(actual, expected, desc);
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        if (curStepRes != null)
            curStepRes.setExpected(expectedResultText);
        if (!actual.equals(expected)) {
            if (curStepRes != null)
                curStepRes.setActual(actual.toString());
            stepLog.assertFail(desc);
        }
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        softAssert.assertNotEquals(actual, expected, desc);
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        if (curStepRes != null)
            curStepRes.setExpected(expectedResultText);
        if (actual.equals(expected)) {
            if (curStepRes != null)
                curStepRes.setActual(actual.toString());
            stepLog.assertFail(desc);
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
            stepLog.assertFail(desc);
        }
    }

    public void isNull(Object object, String desc) {
        softAssert.assertNull(object, desc);
        if (object != null) {
            stepLog.assertFail(desc);
        }
    }

    public void isNotNull(Object object, String desc) {
        softAssert.assertNotNull(object, desc);
        if (object == null) {
            stepLog.assertFail(desc);
        }
    }

    public void isElementTextEqual(Element element, String expectedText) {
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " is not visible";
        softAssert.assertTrue(elementVisibility, desc);
        if (!elementVisibility)
            stepLog.assertFail(desc);
        else {
            String actualText = element.getText();
            String desc2 = element.getMetaName() + " has incorrect text";
            softAssert.assertEquals(actualText, expectedText,
                    desc2);
            if (!actualText.equals(expectedText)) {
                stepLog.assertFail(desc2);
            }
        }
    }

    public void verifyStep() {
        softAssert.assertAll();
    }

}
