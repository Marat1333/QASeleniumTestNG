package com.leroy.core.web_elements.general;

import com.google.common.collect.ImmutableMap;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public class EditBox extends Element {

    // ------ CONSTRUCTORS ------ //

    public EditBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    // ------ PRIVATE METHODS ------ //

    // ------ PUBLIC METHODS ------ //

    /**
     * Is enabled?
     *
     * @return true/false
     */
    public boolean isEnabled() {
        initialWebElementIfNeeded();
        try {
            return webElement.isEnabled();
        } catch (WebDriverException err) {
            Log.warn("isEnabled() - " + err.getMessage());
            return webElement.isEnabled();
        }
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean useBackSpace) {
        initialWebElementIfNeeded();
        if (DriverFactory.isAppProfile())
            click();
        waitForVisibility();
        if (!useBackSpace)
            webElement.clear();
        else {
            int length = webElement.getAttribute("value").length();
            for (int i = 0; i < length; i++) {
                webElement.sendKeys(Keys.BACK_SPACE);
            }
        }
    }

    @Override
    public String getText() {
        initialWebElementIfNeeded();
        if (DriverFactory.isAppProfile())
            return super.getText();
        else
            return webElement.getAttribute("value");
    }

    public EditBox fill(String text) {
        initialWebElementIfNeeded();
        if (DriverFactory.isAppProfile())
            waitForClickability(short_timeout, 1);
        webElement.sendKeys(text);
        return this;
    }

    public EditBox fill(String text, boolean imitateTyping) {
        if (!imitateTyping) {
            this.fill(text);
        } else {
            for (int i = 0; i < text.length(); ++i) {
                this.fill(Character.toString(text.charAt(i)));

            }
        }
        return this;
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
            submit();
        }
    }

    public EditBox submit() {
        if (DriverFactory.isAppProfile())
            ((AndroidDriver) driver).executeScript(
                    "mobile: performEditorAction", ImmutableMap.of("action", "search"));
        else
            webElement.sendKeys(Keys.ENTER);
        return this;
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
