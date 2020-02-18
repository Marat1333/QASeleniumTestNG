package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

public class RadioButton extends Checkbox {
    public RadioButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }
}
