package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

public class RadioButton extends Element{
    public RadioButton(WebDriver driver, CustomLocator locator){
        super(driver, locator);
    }

    public boolean isChecked() throws Exception {
        initialWebElementIfNeeded();
        waitForVisibility(short_timeout);
        return webElement.isSelected();
    }
}
