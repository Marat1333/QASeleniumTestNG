package com.leroy.core.asserts;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;

import java.util.Arrays;

public class CustomAssert extends BaseCustomAssert {

    public CustomAssert(StepLog stepLog) {
        super(stepLog);
    }

    public void isTrue(boolean condition, String desc) {
        super.logIsTrue(condition, desc, false);
    }

    public void isTrue(boolean condition, String actualResult, String expectedResult) {
        super.logIsTrue(condition, actualResult, expectedResult, false);
    }

    public void isFalse(boolean condition, String desc) {
        super.logIsFalse(condition, desc, false);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        super.logIsEquals(actual, expected, desc, false);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        super.logIsNotEquals(actual, expected, desc, false);
    }

    public void isTextContainsIgnoringCase(String actual, String expected, String desc){
        super.logIsTextContainsIgnoringCase(actual, expected, desc, false);
    }

    public void isNull(Object object, String actualResult, String expectedResult) {
        super.logIsNull(object, actualResult, expectedResult, false);
    }

    public void isNotNull(Object object, String actualResult, String expectedResult) {
        super.logIsNotNull(object, actualResult, expectedResult, false);
    }

    public void isElementTextEqual(Element element, String expectedText, String pageSource) {
        super.logIsElementTextEqual(element, expectedText, pageSource, false);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        super.logIsElementTextEqual(element, expectedText, null, false);
    }

    public boolean isElementVisible(BaseWidget element, String pageSource) {
        return super.logIsElementVisible(element, pageSource, false);
    }

    public void isElementTextContains(Element element, String expectedText) {
        super.logIsElementTextContains(element, expectedText, null, false);
    }

    public void isElementTextContains(Element element, String expectedText, String pageSource) {
        super.logIsElementTextContains(element, expectedText, pageSource, false);
    }

    public boolean isElementVisible(BaseWidget element) {
        return isElementVisible(element, null);
    }

    public void areElementsVisible(BaseWidget... elements) {
        logAreElementsVisible(Arrays.asList(elements), false, null);
    }

    public void areElementsVisible(String pageSource, BaseWidget... elements) {
        logAreElementsVisible(Arrays.asList(elements), false, pageSource);
    }

    public boolean isElementNotVisible(BaseWidget element) {
        return super.logIsElementNotVisible(element, null, false);
    }

    public boolean isElementNotVisible(BaseWidget element, String pageSource) {
        return super.logIsElementNotVisible(element, pageSource, false);
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        super.logIsElementImageMatches(elem, pictureName, false);
    }

    public void isElementImageMatches(Element elem, String pictureName, Double expectedPercentage) {
        super.logIsElementImageMatches(elem, pictureName, expectedPercentage, false);
    }

}
