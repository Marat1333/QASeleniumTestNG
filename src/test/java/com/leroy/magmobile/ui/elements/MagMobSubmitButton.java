package com.leroy.magmobile.ui.elements;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class MagMobSubmitButton extends MagMobButton {

    private static final Color ACTIVE_GREEN_COLOR = new Color(102, 192, 93, 255);
    private static final Color INACTIVE_GRAY_COLOR = new Color(202, 206, 210, 255);

    public MagMobSubmitButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isEnabled() {
        try {
            return getPointColor().equals(ACTIVE_GREEN_COLOR);
        } catch (Exception err) {
            Log.error("isEnabled() Error: " + err.getMessage());
            return false;
        }
    }
}
