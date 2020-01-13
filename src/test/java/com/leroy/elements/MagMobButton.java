package com.leroy.elements;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class MagMobButton extends Button {

    public MagMobButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public String getText() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView")).getText();
    }

    @Override
    public Color getPointColor() throws Exception {
        return getPointColor(0, getHeight() / 4);
    }
}
