package com.leroy.elements;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class MagMobButton extends Element {

    private static final Color ACTIVE_GREEN_COLOR = new Color(102, 192, 93, 255);
    private static final Color INACTIVE_GRAY_COLOR = new Color(202, 206, 210, 255);

    public MagMobButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public MagMobButton(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    @Override
    public String getText() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView")).getText();
    }

    @Override
    public Color getColor() throws Exception {
        return getColor(0, getHeight() / 4);
    }

    @Override
    public boolean isEnabled() {
        try {
            return getColor().equals(ACTIVE_GREEN_COLOR);
        } catch (Exception err) {
            Log.error("isEnabled() Error: " + err.getMessage());
            return false;
        }
    }
}
