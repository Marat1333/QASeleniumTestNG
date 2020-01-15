package com.leroy.core.pages;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Element;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.qameta.allure.Step;
import org.openqa.selenium.Dimension;
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
        progressBar.waitForVisibility(tiny_timeout, Duration.ofMillis(200));
    }

    protected void waitForProgressBarIsInvisible() {
        progressBar.waitForInvisibility();
    }

    public void scrollDownTo(Element element) {
        TouchAction action = new TouchAction((AndroidDriver) driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight() - 300;
        int ceil = size.getHeight() - ground;
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;
        while (!element.isVisible()) {
            int breakCounter = 0;
            if (breakCounter > 3) {
                break;
            }
            action.press(PointOption.point(leftBorder, ground)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ceil)).release().perform();
            breakCounter++;
        }
    }

    public void scrollUpTo(Element element) {
        TouchAction action = new TouchAction((AndroidDriver) driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight() - 300;
        int ceil = size.getHeight() - ground;
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;
        while (!element.isVisible()) {
            int breakCounter = 0;
            if (breakCounter > 3) {
                break;
            }
            action.press(PointOption.point(leftBorder, ceil)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ground)).release().perform();
            breakCounter++;
        }
    }

    public void scrollDown() {
        TouchAction action = new TouchAction((AndroidDriver) driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight() - 300;
        int ceil = size.getHeight() - ground;
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;
        action.press(PointOption.point(leftBorder, ground)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ceil)).release().perform();
    }

    public void scrollUp() {
        TouchAction action = new TouchAction((AndroidDriver) driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight() - 300;
        int ceil = size.getHeight() - ground;
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;
        action.press(PointOption.point(leftBorder, ceil)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ground)).release().perform();
    }

    public void swipeRightTo(Element anchorElement, Element goalElement) {
        int anchorY = anchorElement.getLocation().getY();
        Dimension size = androidDriver.manage().window().getSize();
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;

        TouchAction action = new TouchAction((AndroidDriver) driver);
        while (!goalElement.isVisible()) {
            int breakCounter = 0;
            if (breakCounter > 3) {
                break;
            }
            action.press(PointOption.point(rightBorder, anchorY)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, anchorY)).release().perform();
            breakCounter++;
        }
    }

    public void swipeLeftTo(Element anchorElement, Element goalElement) {
        int anchorX = anchorElement.getLocation().getX();
        int anchorY = anchorElement.getLocation().getY();
        Dimension size = androidDriver.manage().window().getSize();
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;

        TouchAction action = new TouchAction((AndroidDriver) driver);
        while (!goalElement.isVisible()) {
            int breakCounter = 0;
            if (breakCounter > 3) {
                break;
            }
            action.press(PointOption.point(anchorX, anchorY)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, anchorY)).release().perform();
            breakCounter++;
        }
    }

    public void swipeRight(Element anchorElement) {
        int anchorX = anchorElement.getLocation().getX();
        int anchorY = anchorElement.getLocation().getY();
        Dimension size = androidDriver.manage().window().getSize();
        int rightBorder = size.getWidth() - 10;

        TouchAction action = new TouchAction((AndroidDriver) driver);
        action.press(PointOption.point(anchorX, anchorY)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(rightBorder, anchorY)).release().perform();
    }

    public void swipeLeft(Element anchorElement) {
        int anchorX = anchorElement.getLocation().getX();
        int anchorY = anchorElement.getLocation().getY();
        Dimension size = androidDriver.manage().window().getSize();
        int rightBorder = size.getWidth() - 10;
        int leftBorder = size.getWidth() - rightBorder;

        TouchAction action = new TouchAction((AndroidDriver) driver);
        action.press(PointOption.point(anchorX, anchorY)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, anchorY)).release().perform();
    }

    // ---------------------- Common Verification Steps ------------------ //

    @Step("Проверить видимость клавиатуры для ввода текста")
    public BaseAppPage shouldKeyboardVisible() {
        anAssert.isTrue(isKeyboardVisible(),
                "Клавиатура для ввода должна быть видна");
        return this;
    }

}
