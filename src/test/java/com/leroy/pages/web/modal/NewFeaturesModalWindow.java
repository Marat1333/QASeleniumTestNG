package com.leroy.pages.web.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NewFeaturesModalWindow extends Element { // TODO Widget

    public NewFeaturesModalWindow(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public NewFeaturesModalWindow(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public NewFeaturesModalWindow(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    public static final String MAIN_DIV_XPATH = "//div[contains(@class, 'WhatIsNewModal')]";

    @WebFindBy(xpath = ".//button[@type='button']")
    private Element submitBtn;

    public void clickSubmitButton() {
        submitBtn.click();
        waitForInvisibility();
    }

}
