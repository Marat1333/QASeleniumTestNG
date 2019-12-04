package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextArea extends Element {

    // ------ CONSTRUCTORS ------ //
    public TextArea(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public TextArea(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    // ------ PRIVATE METHODS ------ //


    // ------ PROTECTED METHODS ------ //


    // ------ PUBLIC METHODS ------ //

    @Override
    public void click() {
        try {
            initialWebElementIfNeeded();
            webElement.click();
        } catch (Exception err) {
            Log.error("click(): " + err.getMessage());
            throw err;
        }
    }

    /**
     * Get text from text area
     *
     * @return String
     */
    @Override
    public String getText() {
        try {
            initialWebElementIfNeeded();
            return webElement.getAttribute("value");
        } catch (Exception err) {
            Log.error("Method: " + getClass().getSimpleName() + ".getText()");
            Log.error("Exception: " + err.getMessage());
            return null;
        }
    }

    /**
     * Is Vertical Scrollbar visible?
     */
    public boolean isVerticalScrollbarVisible() {
        return getCssValue("overflow-y").equals("scroll");
    }

    /**
     * Is Horizontal Scrollbar visible?
     */
    public boolean isHorizontalScrollbarVisible() {
        return getCssValue("overflow-x").equals("scroll");
    }

}
