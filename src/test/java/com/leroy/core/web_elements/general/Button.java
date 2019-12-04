package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Button extends Element {

    public Button(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public Button(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }
}
