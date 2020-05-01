package com.leroy.core.asserts;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.util.ImageUtil;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.List;

public abstract class BaseCustomAssert {

    private StepLog stepLog;
    private SoftAssert softAssert;

    public StepLog getStepLog() {
        return stepLog;
    }

    public BaseCustomAssert(StepLog stepLog) {
        this.stepLog = stepLog;
        softAssert = new SoftAssert();
    }

    private void addResultsToCurrentStepAndThrowAssertException(String actualResult, String expectedResult) {
        StepResultModel curStepRes = stepLog.getCurrentStepResult();
        if (curStepRes != null) {
            curStepRes.addExpectedResult(expectedResult);
            curStepRes.addActualResult(actualResult);
        }
        stepLog.assertFail(actualResult);
    }

    protected void logIsTrue(boolean condition, String desc, boolean isSoft) {
        logIsTrue(condition, desc, "Ожидался противоположный результат полученному", isSoft);
    }

    protected void logIsTrue(boolean condition, String actualResult, String expectedResult, boolean isSoft) {
        if (!condition) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
        if (isSoft)
            softAssert.assertTrue(condition, actualResult);
        else
            Assert.assertTrue(condition, actualResult);
    }

    protected void logIsFalse(boolean condition, String desc, boolean isSoft) {
        logIsFalse(condition, desc, "Ожидался противоположный результат полученному", isSoft);
    }

    protected void logIsFalse(boolean condition, String actualResult, String expectedResult, boolean isSoft) {
        if (condition) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
        if (isSoft)
            softAssert.assertFalse(condition, actualResult);
        else
            Assert.assertFalse(condition, actualResult);
    }

    protected void logIsEquals(Object actual, Object expected, String desc, boolean isSoft) {
        String actualResultText;
        if (desc.contains("%s"))
            actualResultText = String.format(desc, actual.toString());
        else
            actualResultText = desc + " Актуальное значение: " + actual.toString();
        if (!actual.equals(expected)) {
            addResultsToCurrentStepAndThrowAssertException(
                    actualResultText, "Ожидаемое значение: " + expected.toString());
        }
        if (isSoft)
            softAssert.assertEquals(actual, expected, actualResultText);
        else
            Assert.assertEquals(actual, expected, desc);
    }

    protected void logIsNotEquals(Object actual, Object expected, String desc, boolean isSoft) {
        String actualResultText;
        if (desc.contains("%s"))
            actualResultText = String.format(desc, actual.toString());
        else
            actualResultText = desc + " Актуальное значение: " + actual.toString();
        if (actual.equals(expected)) {
            addResultsToCurrentStepAndThrowAssertException(
                    actualResultText, "Значение, которого не ожидалось: " + expected.toString());
        }
        if (isSoft)
            softAssert.assertNotEquals(actual, expected, desc);
        else
            Assert.assertNotEquals(actual, expected, desc);
    }

    protected void logIsNull(Object object, String actualResult, String expectedResult, boolean isSoft) {
        if (object != null) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
        if (isSoft)
            softAssert.assertNull(object, actualResult);
        else
            Assert.assertNull(object, actualResult);
    }

    protected void logIsNotNull(Object object, String actualResult, String expectedResult, boolean isSoft) {
        if (object == null) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
        if (isSoft)
            softAssert.assertNotNull(object, actualResult);
        else
            Assert.assertNotNull(object, actualResult);
    }

    // For UI

    protected boolean logIsElementVisible(BaseWidget element, String pageSource, boolean isSoft) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = pageSource == null ? element.isVisible() : element.isVisible(pageSource);
        String desc = element.getMetaName() + " не отображается";
        if (!elementVisibility) {
            addResultsToCurrentStepAndThrowAssertException(
                    desc,
                    element.getMetaName() + " должен отображаться");
        }
        if (isSoft)
            softAssert.assertTrue(elementVisibility, desc);
        else
            Assert.assertTrue(elementVisibility, desc);
        return elementVisibility;
    }

    protected boolean logIsElementVisible(BaseWidget element, boolean isSoft) {
        return logIsElementVisible(element, null, isSoft);
    }

    protected void logAreElementsVisible(List<BaseWidget> elements, boolean isSoft, String pageSource) {
        if (elements.size() == 0)
            throw new IllegalArgumentException("List should contain at least one element");
        if (pageSource == null && DriverFactory.isAppProfile())
            pageSource = elements.get(0).getPageSource();
        for (BaseWidget elem : elements) {
            logIsElementVisible(elem, pageSource, true);
        }
        if (!isSoft)
            verifyAll();
    }

    protected boolean logIsElementNotVisible(BaseWidget element, String pageSource, boolean isSoft) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = pageSource == null ? element.isVisible() : element.isVisible(pageSource);
        String expectedResult = element.getMetaName() + " не должен отображаться";
        String actualResult = element.getMetaName() + " отображается";
        if (elementVisibility) {
            addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
        }
        if (isSoft)
            softAssert.assertFalse(elementVisibility, actualResult);
        else
            Assert.assertFalse(elementVisibility, actualResult);
        return !elementVisibility;
    }

    protected void logIsElementTextEqual(Element elem, String expectedText, String pageSource, boolean isSoft) {
        if (logIsElementVisible(elem, pageSource, isSoft)) {
            String actualText = elem.getText(pageSource);
            String expectedResult = String.format("Элемент '%s' должен иметь текст '%s'",
                    elem.getMetaName(), expectedText);
            String actualResult = String.format(
                    "Элемент '%s' имеет текст '%s'", elem.getMetaName(), actualText);
            if (!actualText.equals(expectedText)) {
                addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
            }
            if (isSoft)
                softAssert.assertEquals(actualText, expectedText, actualResult);
            else
                Assert.assertEquals(actualText, expectedText, actualResult);
        }
    }

    protected void logIsElementTextContains(Element element, String expectedText, String pageSource, boolean isSoft) {
        if (logIsElementVisible(element, pageSource, isSoft)) {
            String actualText = element.getText(pageSource);
            String expectedResult = String.format("Элемент '%s' должен содержать часть текста '%s'",
                    element.getMetaName(), expectedText);
            String actualResult = String.format(
                    "Элемент '%s' имеет текст '%s'", element.getMetaName(), actualText);
            if (!actualText.contains(expectedText)) {
                addResultsToCurrentStepAndThrowAssertException(actualResult, expectedResult);
            }
            if (isSoft) {
                softAssert.assertTrue(actualText.contains(expectedText), actualResult);
            } else {
                Assert.assertTrue(actualText.contains(expectedText), actualResult);
            }
        }
    }

    protected ImageUtil.CompareResult logIsElementImageMatches(Element elem, String pictureName,
                                                               Double expectedPercentage, boolean isSoft) {
        ImageUtil.CompareResult result = null;
        String actualResult = "Визуально элемент '" + elem.getMetaName() + "' не соответствует эталону";
        String expectedResult = "Визуально элемент '" + elem.getMetaName() + "' должен соответствовать эталону";
        try {
            //ImageUtil.takeScreenShot(elem, pictureName); // Only for taking sample snapshots
            result = ImageUtil.takeScreenAndCompareWithBaseImg(elem, pictureName, expectedPercentage);
        } catch (Exception err) {
            Log.error(err.getMessage());
            result = ImageUtil.CompareResult.ElementNotFound;
        }
        if (!ImageUtil.CompareResult.Matched.equals(result)) {
            addResultsToCurrentStepAndThrowAssertException(
                    actualResult,
                    expectedResult);
        }
        if (isSoft)
            softAssert.assertEquals(result, ImageUtil.CompareResult.Matched,
                    actualResult);
        else
            Assert.assertEquals(result, ImageUtil.CompareResult.Matched,
                    actualResult);
        return result;
    }

    protected ImageUtil.CompareResult logIsElementImageMatches(Element elem, String pictureName, boolean isSoft) {
        return logIsElementImageMatches(elem, pictureName, 99.0, isSoft);
    }

    protected void verifyAll() {
        if (getStepLog().currentStepResult != null)
            if (getStepLog().currentStepResult.getStatus_id() == ResultModel.ST_UNTESTED)
                getStepLog().currentStepResult.setStatus_id(ResultModel.ST_PASSED);
        softAssert.assertAll();
    }

}
