package com.leroy.magmobile.ui.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Checkbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class MagMobGrayCheckBox extends Checkbox {
    private static final Color CHECKED_GRAY_COLOR = new Color(153, 153, 153, 255);
    private static final Color UNCHECKED_WHITE_COLOR = new Color(255, 255, 255, 255);

    public MagMobGrayCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public Color getPointColor() throws Exception {
        return getPointColor(0, -getHeight() / 4);
    }

    @Override
    public boolean isChecked() throws Exception {
        return getPointColor().equals(CHECKED_GRAY_COLOR);
    }
}
