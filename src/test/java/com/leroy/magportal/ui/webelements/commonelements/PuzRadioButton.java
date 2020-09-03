package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import org.openqa.selenium.WebDriver;

public class PuzRadioButton extends Button {

    private static final String ACTIVE_GREEN_COLOUR = "rgba(218, 240, 216, 1)";

    public PuzRadioButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public boolean isSelected() {
        return this.getCssValue("background-color").equals(ACTIVE_GREEN_COLOUR);
    }
}
