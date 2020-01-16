package com.leroy.core.configuration;

import com.leroy.core.asserts.BaseCustomAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.util.ImageUtil;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class CustomSoftAssert extends BaseCustomAssert {

    private SoftAssert softAssert;

    public CustomSoftAssert(StepLog stepLog) {
        super(stepLog);
        softAssert = new SoftAssert();
    }

    public void isTrue(boolean condition, String desc) {
        logIsTrue(condition, desc);
        softAssert.assertTrue(condition, desc);
    }

    public void isFalse(boolean condition, String desc) {
        logIsFalse(condition, desc);
        softAssert.assertFalse(condition, desc);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        logIsEquals(actual, expected, desc);
        softAssert.assertEquals(actual, expected, desc);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        logIsNotEquals(actual, expected, desc);
        softAssert.assertNotEquals(actual, expected, desc);
    }

    public void isNull(Object object, String actualResult, String expectedResult) {
        logIsNull(object, actualResult, expectedResult);
        softAssert.assertNull(object, actualResult);
    }

    public void isNotNull(Object object, String actualResult, String expectedResult) {
        logIsNotNull(object, actualResult, expectedResult);
        softAssert.assertNotNull(object, actualResult);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            logIsElementTextEqual(element.getMetaName(), actualText, expectedText);
            softAssert.assertEquals(actualText, expectedText,
                    String.format("Элемент '%s' содержит текст '%s'", element.getMetaName(), actualText));
        }
    }

    public boolean isElementVisible(BaseWidget element) {
        boolean elementVisibility = logIsElementVisible(element);
        String desc = element.getMetaName() + " должен отображаться";
        softAssert.assertTrue(elementVisibility, desc);
        return elementVisibility;
    }

    public void areElementsVisible(List<BaseWidget> elements) {
        for (BaseWidget elem : elements) {
            isElementVisible(elem);
        }
    }

    public boolean isElementNotVisible(BaseWidget element) {
        boolean elementVisibility = !(logIsElementNotVisible(element));
        String desc = element.getMetaName() + " не должен отображаться";
        softAssert.assertFalse(elementVisibility, desc);
        return !elementVisibility;
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        ImageUtil.CompareResult result = logIsElementImageMatches(elem, pictureName);
        softAssert.assertEquals(result, ImageUtil.CompareResult.Matched,
                "Визуально элемент '" + elem.getMetaName() + "' не соответствует эталону");
    }

    public void verifyAll() {
        if (getStepLog().currentStepResult != null)
            if (getStepLog().currentStepResult.getStatus_id() == ResultModel.ST_UNTESTED)
                getStepLog().currentStepResult.setStatus_id(ResultModel.ST_PASSED);
        softAssert.assertAll();
    }

}
