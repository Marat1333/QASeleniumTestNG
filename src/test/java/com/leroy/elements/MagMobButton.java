package com.leroy.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MagMobButton extends Element {

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
}
