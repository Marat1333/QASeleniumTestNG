package com.leroy.core.web_elements.general;

import com.google.common.collect.ImmutableMap;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.fieldfactory.CustomLocator;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class EditBox extends Element {

    // ------ CONSTRUCTORS ------ //

    public EditBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public EditBox(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    // ------ PRIVATE METHODS ------ //

    // ------ PUBLIC METHODS ------ //

    public void clear() {
        initialWebElementIfNeeded();
        if (DriverFactory.isAppProfile() && !isEnabled())
            click();
        waitForVisibility();
        webElement.clear();
    }

    @Override
    public String getText() {
        initialWebElementIfNeeded();
        if (DriverFactory.isAppProfile())
            return super.getText();
        else
            return webElement.getAttribute("value");
    }

    public void fill(String text) {
        initialWebElementIfNeeded();
        webElement.sendKeys(text);
    }

    public void fill(String text, boolean imitateTyping) {
        if (!imitateTyping) {
            this.fill(text);
        } else {
            for (int i = 0; i < text.length(); ++i) {
                this.fill(Character.toString(text.charAt(i)));
            }
        }
    }

    public void clearAndFill(String text) {
        clearAndFill(text, false);
    }

    public void clearAndFill(String text, boolean imitateTyping) {
        if (text != null) {
            clear();
            fill(text, imitateTyping);
        }
    }

    public void clearFillAndSubmit(String text) {
        if (text != null) {
            clear();
            fill(text);
            if (DriverFactory.isAppProfile())
                ((AndroidDriver) driver).executeScript(
                        "mobile: performEditorAction", ImmutableMap.of("action", "search"));
            else
                webElement.sendKeys(Keys.ENTER);
        }
    }

    public void sendBlurEvent() {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) this.driver;
            jsExecutor.executeScript("arguments[0].blur();", new Object[]{this.webElement});

        } catch (Exception var2) {
            throw var2;
        }
    }
}
