package com.leroy.core.pages;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Element;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriverException;

import java.time.Duration;

public class BaseAppPage extends BasePage {

    AndroidDriver<MobileElement> androidDriver;

    @AppFindBy(xpath = "//android.widget.ProgressBar", cacheLookup = false, metaName = "Progress bar")
    private Element progressBar;

    public BaseAppPage(TestContext context) {
        super(context);
        androidDriver = (AndroidDriver) driver;
    }

    protected boolean isKeyboardVisible() {
        try {
            return androidDriver.isKeyboardShown();
        } catch (WebDriverException err) {
            Log.warn("isKeyboardVisible() method: " + err.getMessage());
            return isKeyboardVisible();
        }
    }

    protected void hideKeyboard() {
        androidDriver.hideKeyboard();
    }

    protected void waitForProgressBarIsVisible() {
        progressBar.waitForVisibility(tiny_timeout, Duration.ofMillis(100));
    }

    protected void waitForProgressBarIsInvisible() {
        progressBar.waitForInvisibility();
    }

    // ---------------------- Common Verification Steps ------------------ //

    @Step("Проверить видимость клавиатуры для ввода текста")
    public BaseAppPage shouldKeyboardVisible() {
        anAssert.isTrue(isKeyboardVisible(),
                "Клавиатура для ввода должна быть видна");
        return this;
    }

}
