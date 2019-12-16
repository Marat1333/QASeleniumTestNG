package com.leroy.core.web_elements.general;

import com.leroy.core.BaseContainer;
import com.leroy.core.fieldfactory.CustomFieldElementLocator;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import java.util.List;

public abstract class BaseElement extends BaseContainer {

    protected CustomLocator locator;

    public BaseElement(WebDriver driver) {
        super(driver);
    }

    public BaseElement(WebDriver driver, CustomLocator locator) {
        super(driver);
        this.locator = locator;
        initElements(locator);
    }

    public boolean isCacheLookup() {
        return locator.isCacheLookup();
    }

    /**
     * Get xpath of the element
     *
     * @return String
     */
    public String getXpath() {
        return XpathUtil.getXpath(locator.getBy());
    }

}
