package com.leroy.magportal.ui.pages.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewFeaturesModalWindow extends BaseWidget {

    public NewFeaturesModalWindow(WebDriver driver) {
        super(driver, new CustomLocator(By.xpath(MAIN_DIV_XPATH)));
    }

    private static final String MAIN_DIV_XPATH = "//div[contains(@class, 'WhatIsNewModal')]";

    @WebFindBy(xpath = ".//button[@type='button']")
    private Element submitBtn;

    public void clickSubmitButton() {
        submitBtn.click();
        waitForInvisibility();
    }

}
