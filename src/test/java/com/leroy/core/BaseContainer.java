package com.leroy.core;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomFieldElementLocator;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.fieldfactory.FieldInitializer;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BaseContainer {

    protected WebDriver driver;
    protected int tiny_timeout = 2;
    protected int short_timeout = 5;
    protected int timeout = 30;
    protected int long_timeout = 60;

    public BaseContainer(WebDriver driver) {
        this.driver = driver;
    }

    protected void initElements() {
        initElements(null);
    }

    protected void initElements(CustomLocator locator) {
        Class<?> current = this.getClass();
        while (current.getSuperclass() != null) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getAnnotation(WebFindBy.class) != null || field.getAnnotation(AppFindBy.class) != null) {
                    FieldInitializer decorator = new FieldInitializer(driver, field, locator);
                    Object value = decorator.initField();
                    if (value != null) {
                        field.setAccessible(true);
                        try {
                            field.set(this, value);
                        } catch (IllegalAccessException err) {
                            Log.error("initElements error: " + err.getMessage());
                        }
                    }
                }
            }
            current = current.getSuperclass();
        }
    }

    /**
     * Find element with default timeout
     *
     * @param by - locator
     * @return WebElement
     */
    protected WebElement findElement(By by) {
        return this.driver.findElement(by);
    }

    /**
     * Find element with the specific timeout
     *
     * @param by      - locator
     * @param timeout - timeout
     * @return WebElement
     */
    protected WebElement findElement(By by, int timeout) {
        this.setImplicitWait(timeout);
        WebElement we = this.driver.findElement(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    protected WebElement findElement(CustomLocator locator) {
        if (Strings.isNullOrEmpty(locator.getAccessibilityId()))
            return findElement(locator.getBy());
        else
            return ((AndroidDriver)driver).findElementByAccessibilityId(locator.getAccessibilityId());
    }

    protected WebElement findElement(CustomLocator locator, int timeout) {
        this.setImplicitWait(timeout);
        WebElement we = findElement(locator);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    /**
     * Finds list of web elements
     *
     * @param by
     * @return
     */
    protected List<WebElement> findElements(By by) {
        return this.driver.findElements(by);
    }

    protected List<WebElement> findElements(By by, int timeout) {
        this.setImplicitWait(timeout);
        List<WebElement> we = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    protected List<WebElement> findElements(CustomLocator locator) {
        if (Strings.isNullOrEmpty(locator.getAccessibilityId()))
            return driver.findElements(locator.getBy());
        else
            return ((AndroidDriver)driver).findElementsByAccessibilityId(locator.getAccessibilityId());
    }

    protected List<WebElement> findElements(CustomLocator locator, int timeout) {
        this.setImplicitWait(timeout);
        List<WebElement> weList = findElements(locator);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return weList;
    }

    /**
     * Set Implicity Wait
     *
     * @param seconds - seconds
     */
    protected void setImplicitWait(int seconds) {
        this.driver.manage().timeouts().implicitlyWait((long) seconds, TimeUnit.SECONDS);
    }

    /**
     * Explicit wait. Not recommended for using!
     *
     * @param secs
     * @throws InterruptedException
     */
    protected void wait(int secs) throws InterruptedException {
        int time = secs * 1000;
        try {
            Thread.sleep((long) time);
        } catch (InterruptedException var3) {
            Log.error("Method: wait");
            Log.error("Error: There was a problem forcing to explicit wait");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    protected boolean isDesktop() {
        String browser = DriverFactory.BROWSER_PROFILE;
        return !(browser.contains("android") || browser.contains("ios") || browser.contains("mobile"));
    }

    /**
     * Is browser Chrome?
     *
     * @return
     */
    protected boolean isChrome() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.DESKTOP_CHROME_PROFILE);
    }

    /**
     * Is browser Microsoft Edge?
     *
     * @return
     */
    protected boolean isEdge() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.DESKTOP_EDGE_PROFILE);
    }

    /**
     * Is browser Safari - ios?
     *
     * @return
     */
    protected boolean isIos() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.IOS_PROFILE);
    }

    /**
     * Is browser Chrome Mobile?
     *
     * @return
     */
    protected boolean isAndroid() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.ANDROID_BROWSER_PROFILE);
    }

    /**
     * Is browser Safari - desktop?
     *
     * @return
     */
    protected boolean isSafari() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.DESKTOP_SAFARI_PROFILE);
    }
}
