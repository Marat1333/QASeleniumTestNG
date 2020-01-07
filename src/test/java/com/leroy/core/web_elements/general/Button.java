package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public class Button extends Element {

    public Button(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    /**
     * Is enabled?
     *
     * @return true/false
     */
    public boolean isEnabled() {
        initialWebElementIfNeeded();
        try {
            return webElement.isEnabled();
        } catch (WebDriverException err) {
            Log.warn("isEnabled() - " + err.getMessage());
            return webElement.isEnabled();
        }
    }
}
