package com.leroy.core.configuration;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class CustomSoftAssert {

    private SoftAssert softAssert;
    private StepLog stepLog;
    private boolean isVerifyAll = false;

    public boolean isVerifyAll() {
        return isVerifyAll;
    }

    public CustomSoftAssert(StepLog stepLog) {
        softAssert = new SoftAssert();
        this.stepLog = stepLog;
    }

    public void isTrue(boolean condition, String desc) {
        softAssert.assertTrue(condition, desc);
        if (!condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
            stepLog.assertFail(desc);
        }
    }

    public void isFalse(boolean condition, String desc) {
        softAssert.assertFalse(condition, desc);
        if (condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
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
        if (!actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
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
        if (actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
            stepLog.assertFail(desc);
        }
    }

    public void isNull(Object object, String desc) {
        if (object != null) {
            stepLog.assertFail(desc);
        }
        softAssert.assertNull(object, desc);
    }

    public void isNotNull(Object object, String desc) {
        if (object == null) {
            stepLog.assertFail(desc);
        }
        softAssert.assertNotNull(object, desc);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            String desc2 = element.getMetaName() + String.format(" должно содержать текст **%s**", expectedText);
            softAssert.assertEquals(actualText, expectedText,
                    desc2);
            if (!actualText.equals(expectedText)) {
                StepResultModel curStepRes = stepLog.getCurrentStepResult();
                curStepRes.addExpectedResult(desc2);
                stepLog.assertFail(desc2);
                curStepRes.addActualResult(element.getMetaName() + String.format(" содержит текст **%s**", actualText));
            }
        }
    }

    public boolean isElementVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " должен отображаться";
        softAssert.assertTrue(elementVisibility, desc);
        if (!elementVisibility) {
            stepLog.assertFail(desc);
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult(element.getMetaName() + " **не** отображается");
            return false;
        } else
            return true;
    }

    public void verifyAll() {
        if (stepLog.currentStepResult != null)
            if (stepLog.currentStepResult.getStatus_id() == ResultModel.ST_UNTESTED)
                stepLog.currentStepResult.setStatus_id(ResultModel.ST_PASSED);
        isVerifyAll = true;
        softAssert.assertAll();
    }

}
