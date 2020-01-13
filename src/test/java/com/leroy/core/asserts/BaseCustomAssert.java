package com.leroy.core.asserts;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.util.ImageUtil;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;

public abstract class BaseCustomAssert {

    private StepLog stepLog;

    public StepLog getStepLog() {
        return stepLog;
    }

    public BaseCustomAssert(StepLog stepLog) {
        this.stepLog = stepLog;
    }

    private void addResultsToCurrentStepAndThrowAssertException(String actualResult, String expectedResult) {
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        curStepRes.addExpectedResult(expectedResult);
        curStepRes.addActualResult(actualResult);
        stepLog.assertFail(actualResult);
    }

    protected void logIsTrue(boolean condition, String desc) {
        logIsTrue(condition, desc, "Ожидался противоположный результат полученному");
    }

    protected void logIsTrue(boolean condition, String actualResult, String expectedResult) {
        if (!condition) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
    }

    protected void logIsFalse(boolean condition, String desc) {
        logIsFalse(condition, desc, "Ожидался противоположный результат полученному");
    }

    protected void logIsFalse(boolean condition, String actualResult, String expectedResult) {
        if (condition) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
    }

    protected void logIsEquals(Object actual, Object expected, String desc) {
        String actualResultText;
        if (desc.contains("%s"))
            actualResultText = String.format(desc, actual.toString());
        else
            actualResultText = desc + " Актуальное значение: " + actual.toString();
        if (!actual.equals(expected)) {
            addResultsToCurrentStepAndThrowAssertException(
                    actualResultText, "Ожидаемое значение: " + expected.toString());
        }
    }

    protected void logIsNotEquals(Object actual, Object expected, String desc) {
        String actualResultText;
        if (desc.contains("%s"))
            actualResultText = String.format(desc, actual.toString());
        else
            actualResultText = desc + " Актуальное значение: " + actual.toString();
        if (actual.equals(expected)) {
            addResultsToCurrentStepAndThrowAssertException(
                    actualResultText, "Ожидаемое значение: " + expected.toString());
        }
    }

    protected void logIsNull(Object object, String actualResult, String expectedResult) {
        if (object != null) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
    }

    protected void logIsNotNull(Object object, String actualResult, String expectedResult) {
        if (object == null) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
    }

    // For UI

    protected boolean logIsElementVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        if (!elementVisibility) {
            addResultsToCurrentStepAndThrowAssertException(
                    element.getMetaName() + " не отображается",
                    element.getMetaName() + " должен отображаться");
        }
        return elementVisibility;
    }

    protected boolean logIsElementNotVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        String expectedResult = element.getMetaName() + " не должен отображаться";
        String actualResult = element.getMetaName() + " отображается";
        if (elementVisibility) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResult);
            curStepRes.addActualResult(actualResult);
            stepLog.assertFail(actualResult);
            return false;
        } else
            return true;
    }

    protected void logIsElementTextEqual(String elemName, String actualText, String expectedText) {
        String expectedResult = String.format("Элемент '%s' должен содержать текст '%s'",
                elemName, expectedText);
        String actualResult = String.format(
                "Элемент '%s' содержит текст '%s'", elemName, actualText);
        if (!actualText.equals(expectedText)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResult);
            curStepRes.addActualResult(actualResult);
            stepLog.assertFail(actualResult);
        }
    }

    protected ImageUtil.CompareResult logIsElementImageMatches(Element elem, String pictureName) {
        ImageUtil.CompareResult result = null;
        String desc = "Визуально элемент '" + elem.getMetaName() + "' должен соответствовать эталону";
        try {
            //ImageUtil.takeScreenShot(elem, pictureName); // Only for taking sample snapshots
            result = ImageUtil.takeScreenAndCompareWithBaseImg(elem, pictureName);
        } catch (Exception err) {
            Log.error(err.getMessage());
            Assert.fail("Couldn't take screenshot for " + elem.getMetaName());
        }
        if (!ImageUtil.CompareResult.Matched.equals(result)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Визуально элемент '" + elem.getMetaName() + "' не соответствует эталону");
            stepLog.assertFail(desc);
        }
        return result;
    }


}
