package com.leroy.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import java.awt.Color;

public class MagMobOvalCheckBox extends Element {
    public static final Color ACTIVE_GREEN_COLOR = new Color(102, 192, 93, 255);

    public MagMobOvalCheckBox (WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public MagMobOvalCheckBox(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public static Color getActiveGreenColor(){
        return ACTIVE_GREEN_COLOR;
    }
}
