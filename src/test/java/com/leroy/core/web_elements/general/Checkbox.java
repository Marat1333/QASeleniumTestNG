package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

public class Checkbox extends Element {

    public Checkbox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public Checkbox(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public boolean isChecked() throws Exception {
        initialWebElementIfNeeded();
        waitForVisibility(short_timeout);
        return webElement.isSelected();
    }

    public void setValue(boolean value) throws Exception {
        this.waitForVisibility(short_timeout);
        if (value ^ this.isChecked()) {
            this.click();
        }
    }


}