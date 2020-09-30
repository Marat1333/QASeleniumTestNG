package com.leroy.magmobile.ui.pages.work.ruptures.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magmobile.ui.elements.MagMobRadioButton;
import org.openqa.selenium.WebDriver;

public class RuptureRadioButton extends MagMobRadioButton {
    public RuptureRadioButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isChecked() throws Exception {
        return getPointColor(getWidth() / 4, 0).equals(CHECKED_GREEN_COLOR);
    }
}
