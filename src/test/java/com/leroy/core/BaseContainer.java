package com.leroy.core;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.fieldfactory.FieldInitializer;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;
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

    public WebDriver getDriver() {
        return driver;
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
            return ((AndroidDriver) driver).findElementByAccessibilityId(locator.getAccessibilityId());
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
            return ((AndroidDriver) driver).findElementsByAccessibilityId(locator.getAccessibilityId());
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
        this.driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
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
            Thread.sleep(time);
        } catch (InterruptedException var3) {
            Log.error("Method: wait");
            Log.error("Error: There was a problem forcing to explicit wait");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    /**
     * Get Source from Page / Screen
     * @return page source
     */
    public String getPageSource() {
        try {
            return driver.getPageSource();
        } catch (WebDriverException err) {
            Log.warn(err.getMessage());
            return driver.getPageSource();
        }
    }

    protected CustomLocator buildLocator(String str, String metaName) {
        CustomLocator locator;
        if (str.startsWith("/")) {
            locator = new CustomLocator(By.xpath(str), metaName);
        } else if (str.startsWith("#")) {
            locator = new CustomLocator(By.cssSelector(str), metaName);
        } else if (str.startsWith("$")) {
            locator = new CustomLocator(str.replaceFirst("\\$", ""), metaName);
        } else if (str.contains("contains(")) {
            String _xpathTmp = DriverFactory.isAppProfile() ? "//*[contains(@text, %s)]" : "//*[contains(text(),%s)]";
            String subStr = StringUtils.substringBetween(str, "contains(", ")");
            locator = new CustomLocator(By.xpath(String.format(_xpathTmp, Quotes.escape(subStr))),
                    metaName == null ? String.format("Элемент содержащий текст %s", Quotes.escape(subStr)) : metaName);
        } else {
            String _xpathTmp = DriverFactory.isAppProfile() ? "//*[@text='%s']" : "//*[text()='%s']";
            locator = new CustomLocator(By.xpath(String.format(_xpathTmp, str)),
                    metaName == null ? String.format("Элемент с текстом '%s'", str) : metaName);
        }
        return locator;
    }

    public <T extends BaseWidget> T E(String str, String metaName, Class<? extends BaseWidget> clazz) {
        CustomLocator locator = buildLocator(str, metaName);
        try {
            return (T) clazz.getConstructor(WebDriver.class, CustomLocator.class)
                    .newInstance(driver, locator);
        } catch (Exception err) {
            Log.error(err.getMessage());
            return null;
        }
    }

    public Element E(String str, String metaName) {
        return E(str, metaName, Element.class);
    }

    public <T extends BaseWidget> T E(String str, Class<? extends BaseWidget> clazz) {
        return E(str, null, clazz);
    }

    public Element E(String str) {
        return E(str, null, Element.class);
    }

    public <E extends BaseWidget> ElementList<E> EL(String str, String metaName, Class<? extends BaseWidget> clazz) {
        CustomLocator locator = buildLocator(str, metaName);
        try {
            return new ElementList<>(driver, locator, clazz);
        } catch (Exception err) {
            Log.error(err.getMessage());
            return null;
        }
    }

    public ElementList<Element> EL(String str, String metaName) {
        return EL(str, metaName, Element.class);
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
     * Is browser Safari - desktop?
     *
     * @return
     */
    protected boolean isSafari() {
        return DriverFactory.BROWSER_PROFILE.equals(DriverFactory.DESKTOP_SAFARI_PROFILE);
    }
}
