package com.leroy.core.pages;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Element;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.HashMap;

public class BaseAppPage extends BasePage {

    AndroidDriver<MobileElement> androidDriver;
    private JavascriptExecutor javascriptExecutor;

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

    public void scrollTo(Element element){
        //TouchAction action = new TouchAction((AndroidDriver)driver);
        androidDriver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains("+element.getText()+").instance(0))");
        /*javascriptExecutor = (JavascriptExecutor) driver;
        HashMap<String,String> scrollObject = new HashMap<>();
        scrollObject.put("direction","down");
        javascriptExecutor.executeScript("mobile:scroll", scrollObject);*/

        //driver.findElementByAndroidUIAutomator(new UiScrollable(new UiSelector().scrollable(true).instance(0))" + ".scrollIntoView(new UiSelector().resourceId(\" + elemId + \").instance(0))");
        System.out.println(1);
    }


    public void scrollUp(){
        javascriptExecutor = (JavascriptExecutor) driver;
        HashMap<String,String> scrollObject = new HashMap<>();
        scrollObject.put("direction","up");
        javascriptExecutor.executeScript("mobile: scroll", scrollObject);
    }

    // ---------------------- Common Verification Steps ------------------ //

    @Step("Проверить видимость клавиатуры для ввода текста")
    public BaseAppPage shouldKeyboardVisible() {
        anAssert.isTrue(isKeyboardVisible(),
                "Клавиатура для ввода должна быть видна");
        return this;
    }

}
