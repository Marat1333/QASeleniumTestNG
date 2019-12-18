package com.leroy.core.configuration;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;

public class CustomAssert {

    private StepLog stepLog;

    public CustomAssert(StepLog stepLog) {
        this.stepLog = stepLog;
    }

    public void isTrue(boolean condition, String desc) {
        if (!condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
            stepLog.assertFail(desc);
        }
        Assert.assertTrue(condition, desc);
    }

    public void isFalse(boolean condition, String desc) {
        if (condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
            stepLog.assertFail(desc);
        }
        Assert.assertFalse(condition, desc);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        if (!actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
            stepLog.assertFail(expectedResultText);
        }
        Assert.assertEquals(actual, expected, expectedResultText);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        if (actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
            stepLog.assertFail(expectedResultText);
        }
        Assert.assertNotEquals(actual, expected, expectedResultText);
    }

    public void isNull(Object object, String desc) {
        if (object != null) {
            stepLog.assertFail(desc);
        }
        Assert.assertNull(object, desc);
    }

    public void isNotNull(Object object, String desc) {
        if (object == null) {
            stepLog.assertFail(desc);
        }
        Assert.assertNotNull(object, desc);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            String desc2 = element.getMetaName() + String.format(" должно содержать текст **%s**", expectedText);
            if (!actualText.equals(expectedText)) {
                StepResultModel curStepRes = stepLog.getCurrentStepResult();
                curStepRes.addExpectedResult(desc2);
                stepLog.assertFail(desc2);
                curStepRes.addActualResult(element.getMetaName() + String.format(" содержит текст **%s**", actualText));
            }
            Assert.assertEquals(actualText, expectedText,
                    desc2);
        }
    }

    public boolean isElementVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " должен отображаться";
        if (!elementVisibility) {
            stepLog.assertFail(desc);
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult(element.getMetaName() + " **не** отображается");
            Assert.fail(desc);
            return false;
        } else
            return true;
    }

}
