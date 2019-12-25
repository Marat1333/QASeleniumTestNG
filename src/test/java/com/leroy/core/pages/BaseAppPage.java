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
import org.openqa.selenium.By;
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
        progressBar.waitForVisibility(tiny_timeout, Duration.ofMillis(100));
    }

    protected void waitForProgressBarIsInvisible() {
        progressBar.waitForInvisibility();
    }

    public void scrollDownTo(Element element){
        TouchAction action = new TouchAction((AndroidDriver)driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight()-50;
        int ceil = size.getHeight() - ground;
        int leftBorder=size.getWidth()-10;
        int rightBorder=size.getWidth()-leftBorder;
        while (!element.isVisible()){
            action.press(PointOption.point(leftBorder, ground)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ceil)).release().perform();
        }
    }

    public void scrollUpTo(Element element){
        TouchAction action = new TouchAction((AndroidDriver)driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight()-50;
        int ceil = size.getHeight() - ground;
        int leftBorder=size.getWidth()-10;
        int rightBorder=size.getWidth()-leftBorder;
        while (!element.isVisible()) {
            action.press(PointOption.point(leftBorder, ceil)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ground)).release().perform();
        }
    }

    public void scrollDown(){
        TouchAction action = new TouchAction((AndroidDriver)driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight()-50;
        int ceil = size.getHeight() - ground;
        int leftBorder=size.getWidth()-10;
        int rightBorder=size.getWidth()-leftBorder;
        action.press(PointOption.point(leftBorder, ground)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ceil)).release().perform();
    }

    public void scrollUp(){
        TouchAction action = new TouchAction((AndroidDriver)driver);
        Dimension size = androidDriver.manage().window().getSize();
        int ground = size.getHeight()-50;
        int ceil = size.getHeight() - ground;
        int leftBorder=size.getWidth()-10;
        int rightBorder=size.getWidth()-leftBorder;
        action.press(PointOption.point(leftBorder, ceil)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(leftBorder, ground)).release().perform();
    }

    public void swipeRightTo(Element anchorElement,Element goalElement){

    }

    public void swipeLeft(){

    }

    // ---------------------- Common Verification Steps ------------------ //

    @Step("Проверить видимость клавиатуры для ввода текста")
    public BaseAppPage shouldKeyboardVisible() {
        anAssert.isTrue(isKeyboardVisible(),
                "Клавиатура для ввода должна быть видна");
        return this;
    }

}
