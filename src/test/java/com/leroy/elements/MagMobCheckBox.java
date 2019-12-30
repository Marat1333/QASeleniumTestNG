package com.leroy.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Checkbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;



public class MagMobCheckBox extends Checkbox {
    private static final Color ACTIVE_GREEN_COLOR = new Color(102, 192, 93, 255);
    private static final Color INACTIVE_GRAY_COLOR = new Color(202, 206, 210, 255);
    private static final Color INACTIVE_WHITE_COLOR = new Color(255, 255, 255, 255);

    public MagMobCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public MagMobCheckBox(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public static Color getActiveGreenColor(){
        return ACTIVE_GREEN_COLOR;
    }

    public static Color getInactiveWhiteColor(){
        return INACTIVE_WHITE_COLOR;
    }

    @Override
    protected Color getPointColor(int xOffset, int yOffset) throws Exception {
        xOffset=xOffset/2;
        yOffset=yOffset/2;
        return super.getPointColor(xOffset, yOffset);
    }

    public boolean isChecked(Color expectedColor) throws Exception{
        Color actualColor = getPointColor(0,-20);
        if (actualColor.equals(expectedColor)) {
            return true;
        }else {
            return false;
        }
    }

}
