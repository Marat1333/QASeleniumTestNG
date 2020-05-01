package com.leroy.core.asserts;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;

import java.util.Arrays;

public class CustomSoftAssert extends BaseCustomAssert {

    public CustomSoftAssert(StepLog stepLog) {
        super(stepLog);
    }

    public void isTrue(boolean condition, String desc) {
        super.logIsTrue(condition, desc, true);
    }

    public void isTrue(boolean condition, String actualResult, String expectedResult) {
        super.logIsTrue(condition, actualResult, expectedResult, true);
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

    public void isElementTextEqual(Element element, String expectedText) {
        super.logIsElementTextEqual(element, expectedText, null, true);
    }

    public void isElementTextContains(Element element, String expectedText) {
        super.logIsElementTextContains(element, expectedText, null, true);
    }

    public void isElementTextContains(Element element, String expectedText, String pageSource) {
        super.logIsElementTextContains(element, expectedText, pageSource, true);
    }

    public boolean isElementVisible(BaseWidget element, String pageSource) {
        return super.logIsElementVisible(element, pageSource, true);
    }

    public boolean isElementVisible(BaseWidget element) {
        return isElementVisible(element, null);
    }

    public void areElementsVisible(BaseWidget... elements) {
        logAreElementsVisible(Arrays.asList(elements), true, null);
    }

    public void areElementsVisible(String pageSource, BaseWidget... elements) {
        logAreElementsVisible(Arrays.asList(elements), true, pageSource);
    }

    public boolean isElementNotVisible(BaseWidget element) {
        return super.logIsElementNotVisible(element, null, true);
    }

    public boolean isElementNotVisible(BaseWidget element, String pageSource) {
        return super.logIsElementNotVisible(element, pageSource, true);
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        super.logIsElementImageMatches(elem, pictureName, true);
    }

    public void isElementImageMatches(Element elem, String pictureName, Double expectedPercentage) {
        super.logIsElementImageMatches(elem, pictureName, expectedPercentage, true);
    }

    public void verifyAll() {
        super.verifyAll();
    }

}
