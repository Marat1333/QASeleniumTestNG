package com.leroy.magmobile.ui.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Checkbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;


public class MagMobCheckBox extends Checkbox {

    private static final Color CHECKED_GREEN_COLOR = new Color(102, 192, 93, 255);
    private static final Color UNCHECKED_GRAY_COLOR = new Color(202, 206, 210, 255);
    private static final Color UNCHECKED_WHITE_COLOR = new Color(255, 255, 255, 255);

    public MagMobCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public Color getPointColor() throws Exception {
        return getPointColor(0, - getHeight()/4);
    }

    @Override
    public boolean isChecked() throws Exception {
        return getPointColor().equals(CHECKED_GREEN_COLOR);
    }

}
