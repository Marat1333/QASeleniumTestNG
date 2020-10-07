package com.leroy.magmobile.ui.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.RadioButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class MagMobRadioButton extends RadioButton {
    protected static final Color CHECKED_GREEN_COLOR = new Color(102, 192, 93, 255);

    public MagMobRadioButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isChecked() throws Exception {
        return getPointColor().equals(CHECKED_GREEN_COLOR);
    }
}
