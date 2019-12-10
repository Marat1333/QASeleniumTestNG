package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.configuration.Log;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public class BaseAppPage extends BaseContainer {

    AndroidDriver<MobileElement> androidDriver;

    public BaseAppPage(WebDriver driver) {
        super(driver);
        androidDriver = (AndroidDriver) driver;
        initElements();
        waitForPageIsLoaded();
    }

    public boolean isKeyboardVisible() {
        try {
            return androidDriver.isKeyboardShown();
        } catch (WebDriverException err) {
            Log.warn("isKeyboardVisible() method: "+ err.getMessage());
            return isKeyboardVisible();
        }
    }

    public void waitForPageIsLoaded() {}

}
