package com.leroy.core.configuration;

import com.leroy.core.asserts.BaseCustomAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;

import java.util.List;

public class CustomSoftAssert extends BaseCustomAssert {

    public CustomSoftAssert(StepLog stepLog) {
        super(stepLog);
    }

    public void isTrue(boolean condition, String desc) {
        super.logIsTrue(condition, desc, true);
    }

    public void isFalse(boolean condition, String desc) {
        super.logIsFalse(condition, desc, true);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        super.logIsEquals(actual, expected, desc, true);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        super.logIsNotEquals(actual, expected, desc, true);
    }

    public void isNull(Object object, String actualResult, String expectedResult) {
        super.logIsNull(object, actualResult, expectedResult, true);
    }

    public void isNotNull(Object object, String actualResult, String expectedResult) {
        super.logIsNotNull(object, actualResult, expectedResult, true);
    }

    public void isElementTextEqual(Element element, String expectedText, String pageSource) {
        super.logIsElementTextEqual(element, expectedText, pageSource, true);
    }

    public boolean isElementVisible(BaseWidget element, String pageSource) {
        return super.logIsElementVisible(element, pageSource, true);
    }

    public boolean isElementVisible(BaseWidget element) {
        return isElementVisible(element, null);
    }

    public void areElementsVisible(List<BaseWidget> elements) {
        if (elements.size() == 0)
            throw new IllegalArgumentException("List should contain at least one element");
        String pageSource = elements.get(0).getDriver().getPageSource();
        for (BaseWidget elem : elements) {
            isElementVisible(elem, pageSource);
        }
    }

    public boolean isElementNotVisible(BaseWidget element) {
        return super.logIsElementNotVisible(element, true);
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        super.logIsElementImageMatches(elem, pictureName, true);
    }

    public void verifyAll() {
        super.verifyAll();
    }

}
