package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.*;

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
        waitForVisibility();
        webElement.clear();
    }

    @Override
    public String getText() {
        initialWebElementIfNeeded();
        return webElement.getAttribute("value");
    }

    public void fill(String text) {
        initialWebElementIfNeeded();
        webElement.sendKeys(text);
    }

    public void fill(String text, boolean imitateTyping) {
        initialWebElementIfNeeded();
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
        clear();
        fill(text, imitateTyping);
        if (!DriverFactory.isAppProfile())
            sendBlurEvent();
    }

    public void clearAndFillAndPressEnter(String text) {
        clear();
        fill(text);
        webElement.sendKeys(Keys.ENTER);
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
