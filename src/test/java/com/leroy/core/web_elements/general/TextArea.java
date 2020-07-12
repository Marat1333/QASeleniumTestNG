package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

public class TextArea extends EditBox {

    // ------ CONSTRUCTORS ------ //
    public TextArea(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    // ------ PRIVATE METHODS ------ //


    // ------ PROTECTED METHODS ------ //


    // ------ PUBLIC METHODS ------ //

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
