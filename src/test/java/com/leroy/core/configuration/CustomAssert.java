package com.leroy.core.configuration;

import com.leroy.core.asserts.BaseCustomAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.util.ImageUtil;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;

public class CustomAssert extends BaseCustomAssert {

    public CustomAssert(StepLog stepLog) {
        super(stepLog);
    }

    public void isTrue(boolean condition, String desc) {
        logIsTrue(condition, desc);
        Assert.assertTrue(condition, desc);
    }

    public void isFalse(boolean condition, String desc) {
        logIsFalse(condition, desc);
        Assert.assertFalse(condition, desc);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        logIsEquals(actual, expected, desc);
        Assert.assertEquals(actual, expected, desc);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        logIsNotEquals(actual, expected, desc);
        Assert.assertNotEquals(actual, expected, desc);
    }

    public void isNull(Object object, String actualResult, String expectedResult) {
        logIsNull(object, actualResult, expectedResult);
        Assert.assertNull(object, actualResult);
    }

    public void isNotNull(Object object, String actualResult, String expectedResult) {
        logIsNotNull(object, actualResult, expectedResult);
        Assert.assertNotNull(object, actualResult);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            logIsElementTextEqual(element.getMetaName(), actualText, expectedText);
            Assert.assertEquals(actualText, expectedText,
                    String.format("Элемент '%s' содержит текст '%s'", element.getMetaName(), actualText));
        }
    }

    public void isElementTextContains(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            logIsElementTextEqual(element.getMetaName(), actualText, expectedText);
            Assert.assertTrue(actualText.contains(expectedText),
                    String.format("Элемент '%s' содержит текст '%s'", element.getMetaName(), actualText));
        }
    }

    public boolean isElementVisible(BaseWidget element) {
        boolean elementVisibility = logIsElementVisible(element);
        String desc = element.getMetaName() + " должен отображаться";
        Assert.assertTrue(elementVisibility, desc);
        return elementVisibility;
    }

    public boolean isElementNotVisible(BaseWidget element) {
        boolean elementVisibility = !(logIsElementNotVisible(element));
        String desc = element.getMetaName() + " не должен отображаться";
        Assert.assertFalse(elementVisibility, desc);
        return !elementVisibility;
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        ImageUtil.CompareResult result = logIsElementImageMatches(elem, pictureName);
        Assert.assertEquals(result, ImageUtil.CompareResult.Matched,
                "Визуально элемент '" + elem.getMetaName() + "' не соответствует эталону");
    }

}
