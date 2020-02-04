package com.leroy.magmobile.ui.elements;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

import java.io.IOException;

public class MagMobWhiteSubmitButton extends MagMobButton {

    private static final Color ACTIVE_WHITE_COLOR = new Color(255, 255, 255, 255);
    private static final Color INACTIVE_GRAY_COLOR = new Color(202, 206, 210, 255);

    public MagMobWhiteSubmitButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isEnabled() {
        try {
            Color color = getPointColor();
            if (color.equals(INACTIVE_GRAY_COLOR))
                return false;
            else if (color.equals(ACTIVE_WHITE_COLOR))
                return true;
            else
                throw new InvalidElementStateException(
                        "Button " + getMetaName() + " has invalid color: " + color.toString());
        } catch (IOException err) {
            Log.error("isEnabled() Error: " + err.getMessage());
            return false;
        }
    }
}

