package com.leroy.magmobile.ui.pages.work.ruptures.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class RuptureTaskContainerCheckBox extends MagMobCheckBox {
    private static final Color CHECKED_GRAY_COLOR = new Color(166, 166, 143, 255);

    public RuptureTaskContainerCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isChecked() throws Exception{
        return getPointColor().equals(CHECKED_GRAY_COLOR);
    }
}
