package com.leroy.magmobile.ui.pages.work.ruptures.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magmobile.ui.elements.MagMobGrayCheckBox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class RuptureTaskContainerGreyCheckBox extends MagMobGrayCheckBox {
    private static final Color CHECKED_GRAY_COLOR = new Color(153, 153, 153, 255);

    public RuptureTaskContainerGreyCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isChecked() throws Exception {
        return getPointColor().equals(CHECKED_GRAY_COLOR);
    }
}
