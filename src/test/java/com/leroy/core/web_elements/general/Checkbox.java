package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Checkbox extends Element  {

    public Checkbox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public Checkbox(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public boolean isChecked() {
        try {
            initialWebElementIfNeeded();
            waitForVisibility(short_timeout);
            return webElement.isSelected();
        } catch (Exception e) {
            Log.error("Method: isChecked(). There was a problem determining if the CheckBox is checked");
            throw e;
        }
    }

    public void setValue(boolean value) {
        this.waitForVisibility(short_timeout);
        if (value ^ this.isChecked()) {
            this.click();
        }
    }

}