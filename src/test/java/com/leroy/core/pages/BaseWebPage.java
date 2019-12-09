package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.File;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class BaseWebPage extends BaseContainer {

    private static final ExpectedCondition<Boolean> NON_EMPTY_PAGE_TITLE = new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver browser) {
            return StringUtils.isNotEmpty(browser.getTitle());
        }

        public String toString() {
            return "Page title to come up";
        }
    };

    private static String screenshotPath = System.getProperty("output.path");
    public static final String TRANSITION_TITLE = "loading";
    public static final String TRANSITION_URL = "about:blank";
    protected String initialHandle;  //stores the window handle obtained when the object was created

    public BaseWebPage(WebDriver driver) {
        super(driver);
        if (isDesktop())
            initialHandle = this.driver.getWindowHandle();
        initElements();
    }

    protected BaseWebPage(WebDriver driver, String expectedUrl, Boolean isURL) throws IllegalStateException {
        this(driver);
        long startTimer = System.currentTimeMillis();
        String actualUrl = null;
        this.waitFor(NON_EMPTY_PAGE_TITLE, (long) timeout);

        for (int i = 0; i < 60; ++i) {
            actualUrl = driver.getCurrentUrl();
            if (actualUrl.contains(expectedUrl)) {
                break;
            }
        }

        long endTimer = System.currentTimeMillis();
        Log.info("URL of current page is " + actualUrl + " Time Taken: " + (endTimer - startTimer) + "ms");
        if (!actualUrl.contains(expectedUrl)) {
            throw new IllegalStateException("Expected page URL: " + expectedUrl + ", actual: " + actualUrl + ", Title: " + driver.getTitle());
        }
    }

    protected BaseWebPage(WebDriver driver, String expectedTitle) throws IllegalStateException {
        this(driver);
        long startTimer = System.currentTimeMillis();
        String actualTitle = this.waitForPageTitle(expectedTitle);
        long endTimer = System.currentTimeMillis();
        Log.info("Title of current page is " + actualTitle + " Time Taken: " + (endTimer - startTimer) + "ms");
        if (!actualTitle.contains(expectedTitle)) {
            throw new IllegalStateException("Expected page title: " + expectedTitle + ", actual: " + actualTitle + ", URL: " + driver.getCurrentUrl());
        }
    }

    public boolean waitUntilTitleContains(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        return (Boolean) wait.until(ExpectedConditions.titleContains(text));
    }

    public boolean waitUntilTitleIs(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        return (Boolean) wait.until(ExpectedConditions.titleIs(text));
    }

    protected void waitFor(ExpectedCondition<Boolean> condition, long timeoutSecs) {
        String message = "Waiting for " + condition + " with timeout " + timeoutSecs;
        Log.info(message);
        (new WebDriverWait(this.driver, timeoutSecs)).withMessage(message).until(condition);
    }

    protected void waitFor(ExpectedCondition<Boolean> condition, long timeoutSecs, long sleepInSecs) {
        String message = "Waiting for " + condition + " with timeout " + timeoutSecs + " secs and polling interval " + sleepInSecs + " secs.";
        (new WebDriverWait(this.driver, timeoutSecs, sleepInSecs * 1000L)).withMessage(message).until(condition);
    }

    private String waitForPageTitle(String expectedTitle) {
        this.waitUntilTitleIs(expectedTitle, timeout);
        return this.driver.getTitle();
    }

    public boolean waitAndTypeOnAlert(String text) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
            wait.until(ExpectedConditions.alertIsPresent());
            this.driver.switchTo().alert().sendKeys(text);
            return true;
        } catch (Exception var3) {
            Log.error("Method: waitAndTypeOnAlert");
            Log.error("Error: Cannot type the desired text on the alert. It may not be available");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public String takeScreenShot(String FileName) {
        try {
            Log.info("Screen shot FileName: " + FileName);
            File scrFile = (File) ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath + File.separator + FileName);
            FileUtils.copyFile(scrFile, destFile);
            return destFile.getAbsolutePath();
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    protected boolean jsExecute(String script) throws InterruptedException {
        try {
            JavascriptExecutor js = (JavascriptExecutor) this.driver;
            js.executeScript(script, new Object[0]);
            return true;
        } catch (Exception var3) {
            Log.error("Method: jsExecute");
            Log.error("Error: There was a problem executing a JavaScript script");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean maximizeWindow() throws InterruptedException {
        try {
            this.driver.manage().window().maximize();
            return true;
        } catch (Exception var2) {
            Log.error("Method: maximizeWindow");
            Log.error("Error: There was a problem trying to maximize the browser window");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateBack() throws InterruptedException {
        try {
            this.driver.navigate().back();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateBack");
            Log.error("Error: There was a problem navigating back on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateForward() throws InterruptedException {
        try {
            this.driver.navigate().forward();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateForward");
            Log.error("Error: There was a problem navigating forward on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateTo(String url) throws InterruptedException {
        try {
            this.driver.get(url);
            return true;
        } catch (Exception var3) {
            Log.error("Method: navigateTo");
            Log.error("Error: [FATAL] There was a problem navigating to " + url);
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    /**
     * Get window inner width
     *
     * @return int width
     */
    public int getWindowWidth() {
        return Integer.valueOf(((JavascriptExecutor) driver).executeScript("return window.innerWidth").toString());
    }

    /**
     * Get window inner height
     *
     * @return int height
     */
    public int getWindowHeight() {
        return Integer.valueOf(((JavascriptExecutor) driver).executeScript("return window.innerHeight").toString());
    }

    private String getUserAgent() {
        return ((JavascriptExecutor) driver).executeScript("return window.navigator.userAgent").toString();
    }

    public boolean resizeWindow(Dimension size) throws InterruptedException {
        try {
            this.driver.manage().window().setSize(size);
            return true;
        } catch (Exception var3) {
            Log.error("Method: resizeWindow");
            Log.error("Error: There was a problem trying to resize the browser window");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToDefaultContent() throws InterruptedException {
        try {
            this.driver.switchTo().defaultContent();
            return true;
        } catch (Exception var2) {
            Log.error("Method: switchToDefaultContent");
            Log.error("Error: There was a problem switching the focus of the window to the default content");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean switchToFrame(String handle) throws InterruptedException {
        try {
            this.driver.switchTo().frame(handle);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to a specific frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToFrame(int index) throws InterruptedException {
        try {
            this.driver.switchTo().frame(index);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to the specified frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToFrame(WebElement webElement) throws InterruptedException {
        try {
            this.driver.switchTo().frame(webElement);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to the specified frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    /**
     * @param windowNumber
     * @return
     * @throws InterruptedException
     * @deprecated - use switchToWindow(String handle)
     */
    public boolean switchToWindow(int windowNumber) throws InterruptedException {
        try {
            return this.switchToWindow(windowNumber, timeout);
        } catch (Exception var3) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    /**
     * Open new tab in browser and switch to it
     *
     * @return
     * @throws InterruptedException
     */
    public void openNewTabAndSwitchTo() {
        try {
            Set<String> openedTabs = driver.getWindowHandles();
            ((JavascriptExecutor) driver).executeScript("window.open()");
            waitUntilNewWindowIsOpened(openedTabs, short_timeout);
            Set<String> newOpenedTabs = driver.getWindowHandles();
            newOpenedTabs.removeAll(openedTabs);
            if (newOpenedTabs.isEmpty()) {
                Log.error("Method: openNewTabAndSwitchTo - new tab is not opened");
                return;
            }
            switchToWindow(newOpenedTabs.iterator().next(), timeout);
        } catch (Exception var3) {
            Log.error("Method: openNewTabAndSwitchTo");
            Log.error("Error: There was a problem to open new browser tab");
            Log.error("Exception: " + var3.getMessage());
        }
    }

    /**
     * @param windowNumber
     * @return
     * @throws InterruptedException
     * @deprecated - use switchToWindow(String handle)
     */
    public boolean switchToWindow(int windowNumber, int timeout) throws InterruptedException {
        try {
            if (timeout > 0) {
                (new WebDriverWait(this.driver, (long) timeout)).until((driver1) -> {
                    return this.driver.getWindowHandles().size() - 1 >= windowNumber;
                });
            }

            Object[] handles = this.driver.getWindowHandles().toArray();
            this.driver.switchTo().window(handles[windowNumber].toString());
            return true;
        } catch (Exception var4) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public boolean switchToWindow() throws InterruptedException {
        try {
            return this.switchToWindow(initialHandle, timeout);
        } catch (Exception var3) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToWindow(String handle) throws InterruptedException {
        try {
            return this.switchToWindow(handle, timeout);
        } catch (Exception var3) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToWindow(String handle, int timeout) throws InterruptedException {
        try {
            if (timeout > 0) {
                (new WebDriverWait(this.driver, (long) timeout)).until((driver1) -> {
                    return this.driver.getWindowHandles().contains(handle);
                });
            }
            if (driver instanceof IOSDriver) {
                ((IOSDriver) this.driver).context("WEBVIEW_" + handle);
            } else {
                this.driver.switchTo().window(handle);
            }
            return true;
        } catch (Exception var4) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in \" + timeout + \" seconds");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public void switchToNewWindow(Set<String> oldHandles) throws Exception {
        Set<String> newHandles = driver.getWindowHandles();
        newHandles.removeAll(oldHandles);
        wait(1); // workaround for safari
        switchToWindow(newHandles.toArray()[0].toString());
    }

    public boolean isTextPresentOnSource(String text) {
        return this.driver.getPageSource().contains(text);
    }

    /**
     * Gets the count of opened windows
     *
     * @return
     * @throws InterruptedException
     */
    public int getWindowCount() throws InterruptedException {
        try {
            Set<String> handles = this.driver.getWindowHandles();
            return handles.toArray().length;
        } catch (Exception var2) {
            Log.error("Method: getWindowHandles");
            Log.error("Error: There was a problem getting the number of frames available to be used");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public Dimension getWindowSize() throws InterruptedException {
        try {
            this.driver.manage().window().setPosition(new Point(0, 0));
            java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension dim = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
            return dim;
        } catch (Exception var3) {
            Log.error("Method: getWindowSize");
            Log.error("Error: There was a problem trying to get the browser window size");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    /**
     * Returns the size of the visible part of the browser window
     *
     * @param includeScrollbars
     * @return Dimension
     */
    public Dimension getWindowClientSize(boolean includeScrollbars) {
        if (includeScrollbars) {
            return new Dimension(
                    Integer.valueOf(((JavascriptExecutor) driver)
                            .executeScript("return (window.innerWidth || 0)").toString()),
                    Integer.valueOf(((JavascriptExecutor) driver)
                            .executeScript("return (window.innerHeight || 0)").toString()));
        } else {
            return new Dimension(
                    Integer.valueOf(((JavascriptExecutor) driver)
                            .executeScript("return document.documentElement.clientWidth").toString()),
                    Integer.valueOf(((JavascriptExecutor) driver)
                            .executeScript("return document.documentElement.clientHeight").toString()));
        }
    }

    /**
     * Returns the size of the visible part of the browser window (minus the size of the scroll bars, if present)
     *
     * @return Dimension
     */
    public Dimension getWindowClientSize() {
        return getWindowClientSize(false);
    }

    /**
     * Get PC monitor resolution
     *
     * @return
     */
    public Dimension getScreenSizeJs() throws Exception {
        return new Dimension(
                Integer.valueOf(((JavascriptExecutor) driver).
                        executeScript("return window.screen.width").toString()),
                Integer.valueOf(((JavascriptExecutor) driver).
                        executeScript("return window.screen.height").toString()));
    }

    /**
     * Gets a distance, in pixels, from a left-upper corner of a screen to a left-upper corner of the browser
     *
     * @return Point
     * @throws Exception
     */
    public Point getWindowPosition() throws Exception {
        try {
            return this.driver.manage().window().getPosition();
        } catch (Exception var3) {
            Log.error("Method: getWindowPosition");
            Log.error("Error: There was a problem trying to get the browser window position");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public String getCurrentTitle() {
        return this.driver.getTitle().trim();
    }

    public String waitAndGetTitle(int sec) throws InterruptedException {
        try {
            Thread.sleep(500L);
            long timeOutMilliSecond = (long) (sec * 1000);
            long startTimer = System.currentTimeMillis();

            for (long endTimer = startTimer; endTimer - startTimer < timeOutMilliSecond; endTimer = System.currentTimeMillis()) {
                String title = this.getCurrentTitle();
                if (title != "") {
                    return title;
                }
            }

            return this.getCurrentTitle();
        } catch (Exception var9) {
            Log.error("Method: waitAndGetTitle");
            Log.error("Error: There was a problem getting the current page title");
            Log.error("Exception: " + var9.getMessage());
            throw var9;
        }
    }

    public String waitAndGetUntilTitleIsDifferent(String title) throws InterruptedException {
        return this.waitAndGetUntilTitleIsDifferent(title, timeout);
    }

    public String waitAndGetUntilTitleIsDifferent(String title, int seconds) throws InterruptedException {
        try {
            String actualTitle = this.getCurrentTitle();
            long startTimer = System.currentTimeMillis();
            long endTimer = startTimer + (long) (seconds * 1000);

            while (actualTitle.equals(title) || actualTitle.equals("loading") || actualTitle.isEmpty()) {
                actualTitle = this.getCurrentTitle();
                if (System.currentTimeMillis() > endTimer) {
                    Log.info("Time out and could not find title so terminating while loop");
                    break;
                }
            }

            return actualTitle;
        } catch (Exception var8) {
            Log.error("Method: waitAndGetUntilTitleIsDifferent");
            Log.error("Error: Unable to wait for different title");
            Log.error("Exception: " + var8.getMessage());
            throw var8;
        }
    }

    public String getCurrentUrl() {
        try {
            return this.driver.getCurrentUrl();
        } catch (Exception var2) {
            Log.error("Method: getCurrentUrl");
            Log.error("Error: [FATAL] There was a problem getting current browser URL");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String waitAndGetUntilURLIsDifferent(String URL) throws InterruptedException {
        return this.waitAndGetUntilURLIsDifferent(URL, timeout);
    }

    public String waitAndGetUntilURLIsDifferent(String URL, int timeout) throws InterruptedException {
        try {
            String actualURL = this.getCurrentUrl();
            long endTimer = System.currentTimeMillis() + (long) (timeout * 1000);

            while (actualURL.equals(URL) || actualURL.equals("about:blank") || actualURL.isEmpty()) {
                actualURL = this.getCurrentUrl();
                if (System.currentTimeMillis() > endTimer) {
                    Log.info("Time out and could not find URL so terminating while loop");
                    break;
                }
            }

            return actualURL;
        } catch (Exception var6) {
            Log.error("Method: waitAndGetUntilURLIsDifferent");
            Log.error("Error: Unable to wait for different URL");
            Log.error("Exception: " + var6.getMessage());
            throw var6;
        }
    }

    public boolean close() throws InterruptedException {
        try {
            this.driver.close();
            return true;
        } catch (Exception var2) {
            Log.error("Method: close");
            Log.error("Error: There was a problem closing the browser window. It may not be available");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean closeAll() throws InterruptedException {
        try {
            this.driver.quit();
            return true;
        } catch (Exception var2) {
            Log.error("Method: closeAll");
            Log.error("There was a problem closing all the browser instances. They may not be available");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public void dismissAlert() {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        wait.until(ExpectedConditions.alertIsPresent());
        this.driver.switchTo().alert().dismiss();
    }

    public void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        wait.until(ExpectedConditions.alertIsPresent());
        this.driver.switchTo().alert().accept();
    }

    public void dismissAlertIfPresent() {
        try {
            this.dismissAlert();
        } catch (Exception var2) {
            Log.info("Expected alert was not present");
        }

    }

    protected boolean isElementPresent(By by) throws InterruptedException {
        this.setImplicitWait(0);
        boolean isPresent = this.driver.findElements(by).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    protected boolean isElementPresent(By by, int timeout) throws InterruptedException {
        this.setImplicitWait(timeout);
        boolean isPresent = this.driver.findElements(by).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    protected boolean isElementVisible(By by) throws InterruptedException {
        this.setImplicitWait(0);
        java.util.List<WebElement> webElementList = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return webElementList.size() > 0 && ((WebElement) webElementList.get(0)).isDisplayed();
    }

    protected boolean isElementVisible(By by, int timeout) throws InterruptedException {
        long timeBeforeWait = System.currentTimeMillis();
        this.setImplicitWait(timeout);
        java.util.List<WebElement> webElementList = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        long timeSpent = (System.currentTimeMillis() - timeBeforeWait) / 1000L;
        if (webElementList.size() > 0) {
            try {
                if (timeSpent < (long) timeout) {
                    (new WebDriverWait(this.driver, (long) timeout - timeSpent)).until(ExpectedConditions.visibilityOf((WebElement) webElementList.get(0)));
                }

                return ((WebElement) webElementList.get(0)).isDisplayed();
            } catch (Exception var9) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void reloadPage() {
        try {
            this.driver.navigate().refresh();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void deleteAllCookiesJs() throws InterruptedException {
        try {
            this.jsExecute("document.cookie.split(\";\").forEach(function(c) { document.cookie = c.replace(/^ +/, \"\").replace(/=.*/, \"=;expires=\" + new Date().toUTCString() + \";path=/\"); });");
        } catch (Exception var2) {
            Log.error("Method: deleteAllCookiesJs");
            Log.error("There was a problem deleting all cookies.");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getAlertText() {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) short_timeout);
        String text = "";
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            text = this.driver.switchTo().alert().getText();
        } catch (TimeoutException e) {
            Log.warn("Method: getAlertText. Alert is not present after timeout");
        }
        return text;
    }

    public boolean isAlertPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, (long) tiny_timeout);
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception Ex) {
            return false;
        }
    }

    // TODO remove
    protected String waitAndGetWhileCssAttributeValueStabilize(final WebElement webElement, final String cssAttributeName) throws Exception {
        (new WebDriverWait(this.driver, (long) timeout)).until(new ExpectedCondition<Boolean>() {
            String oldText = webElement.getCssValue(cssAttributeName);

            public Boolean apply(WebDriver driver) {
                Boolean var3;
                try {
                    Boolean var2 = webElement.getCssValue(cssAttributeName).equals(this.oldText);
                    return var2;
                } catch (Exception var7) {
                    var3 = false;
                } finally {
                    this.oldText = webElement.getCssValue(cssAttributeName);
                }

                return var3;
            }
        });
        return webElement.getCssValue(cssAttributeName);
    }

    public String getCookieValue(String name) throws Exception {
        return URLDecoder.decode(this.driver.manage().getCookieNamed(name).getValue(), "UTF-8");
    }

    protected WebElement getFirstElementWithText(java.util.List<WebElement> listOfWebElements, String text) {
        return this.getFirstElementWithText(listOfWebElements, text, false);
    }

    protected WebElement getFirstElementWithText(List<WebElement> listOfWebElements, String text, boolean caseSensitive) {
        Iterator var4 = listOfWebElements.iterator();

        while (var4.hasNext()) {
            WebElement we = (WebElement) var4.next();
            if (caseSensitive) {
                if (we.getText().trim().equals(text)) {
                    return we;
                }
            } else if (we.getText().trim().equalsIgnoreCase(text)) {
                return we;
            }
        }

        return null;
    }

    /**
     * Waits until window URL equals to the specified
     *
     * @param expectedUrl
     * @return
     * @throws Exception
     */
    public BaseWebPage waitUntilUrlIs(String expectedUrl) throws Exception {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) short_timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> {
                try {
                    return getCurrentUrl().equals(expectedUrl);
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            Log.warn("Method: waitUntilUrlIs() - The current URL is not equal to the specified; timeout: " + short_timeout);
        }
        return this;
    }

    /**
     * Waits until window URL starts with the specified string
     *
     * @param startsWith
     * @return
     * @throws Exception
     */
    public BaseWebPage waitUntilUrlStartsWith(String startsWith) throws Exception {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> {
                try {
                    return getCurrentUrl().startsWith(startsWith);
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            Log.warn("Method: waitUntilUrlStartsWith() - The current URL is not equals to the specified; timeout: "
                    + timeout);
        }
        return this;
    }

    /**
     * Wait until a count of window handles is changed.
     *
     * @param initialHandlesCount
     * @param timeout
     * @return If count of window handles is changed return true, otherwise return false
     * @throws Exception
     */
    protected boolean waitUntilWindowHandlesIsChanged(int initialHandlesCount, int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, (long) timeout)).until((driver) -> {
                try {
                    int actualHandlesCount = getWindowCount();
                    return actualHandlesCount != initialHandlesCount;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            Log.warn(String.format(
                    "Expected condition failed: waitUntilWindowHandlesIsChanged (tried for %d second(s))", timeout));
            return false;
        }
        return true;
    }

    /**
     * Wait for one or more windows open
     *
     * @param initialHandlesCount
     * @param timeout
     * @return If count of window handles is changed return true, otherwise return false
     * @throws Exception
     */
    protected boolean waitUntilNewWindowIsOpened(int initialHandlesCount, int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, (long) timeout)).until((driver) -> {
                try {
                    int actualHandlesCount = getWindowCount();
                    return actualHandlesCount > initialHandlesCount;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            Log.warn(String.format(
                    "Expected condition failed: waitUntilNewWindowIsOpened (tried for %d second(s))", timeout));
            return false;
        }
        return true;
    }

    /**
     * Wait for one or more windows open
     *
     * @param handles windows handles
     * @param timeout
     * @return If count of window handles is changed return true, otherwise return false
     * @throws Exception
     */
    protected boolean waitUntilNewWindowIsOpened(Set<String> handles, int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, (long) timeout)).until((driver) -> {
                Set<String> currentHandles = driver.getWindowHandles();
                try {
                    currentHandles.removeAll(handles);
                    return currentHandles.size() > 0;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            Log.warn(String.format(
                    "Expected condition failed: waitUntilNewWindowIsOpened (tried for %d second(s))", timeout));
            return false;
        }
        return true;
    }

    /**
     * Wait for one or more windows open
     *
     * @param handles windows handles
     * @return If new windows were opened returns true, otherwise returns false
     * @throws Exception
     */
    public boolean waitUntilNewWindowIsOpened(Set<String> handles) throws Exception {
        return waitUntilNewWindowIsOpened(handles, short_timeout);
    }

    /**
     * Wait until a count of window handles equals to a given value.
     *
     * @param expectedHandlesCount
     * @param timeout
     * @return If count of window handles equals expected return true, otherwise return false
     * @throws Exception
     */
    protected boolean waitUntilWindowHandlesIs(int expectedHandlesCount, int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, (long) timeout)).until((driver) -> {
                try {
                    int actualHandlesCount = getWindowCount();
                    return actualHandlesCount == expectedHandlesCount;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            Log.warn(String.format(
                    "Expected condition failed: waitUntilWindowHandlesIs (tried for %d second(s))", timeout));
            return false;
        }
        return true;
    }

    /**
     * Close current window and switch cursor to previous
     *
     * @return
     * @throws Exception
     * @deprecated - you should use closeCurrentWindowAndSwitchToSpecified method for compatibility with Safari
     */
    public void closeCurrentWindowAndSwitchToPrevious() throws Exception {
        closeCurrentWindowAndSwitchToPrevious(isEdge()); //workaround for edge
    }

    /**
     * Close current window and switch cursor to previous
     *
     * @param usingJS
     * @throws Exception
     * @deprecated - you should use closeCurrentWindowAndSwitchToSpecified method for compatibility with Safari
     */
    public void closeCurrentWindowAndSwitchToPrevious(boolean usingJS) throws Exception {
        int winHandles = getWindowCount();
        if (usingJS)
            ((JavascriptExecutor) this.driver).executeScript("window.close();");
        else
            close();
        waitUntilWindowHandlesIsChanged(winHandles, short_timeout);
        switchToWindow(getWindowCount() - 1);

    }

    /**
     * Closes current window and switch to the specified
     *
     * @param window  - the window you want to switch to
     * @param usingJS - whether the window should be closed using javascript
     * @throws Exception
     */
    public void closeCurrentWindowAndSwitchToSpecified(BaseWebPage window, boolean usingJS) throws Exception {
        closeCurrentWindowAndSwitchToSpecified(window.initialHandle, usingJS);
    }

    public void closeCurrentWindowAndSwitchToSpecified(BaseWebPage window) throws Exception {
        if (isEdge())
            if (getCurrentUrl().equals("about:blank"))
                closeCurrentWindowAndSwitchToSpecified(window.initialHandle, false);
            else
                closeCurrentWindowAndSwitchToSpecified(window.initialHandle, true);
        else
            closeCurrentWindowAndSwitchToSpecified(window.initialHandle, false);
    }

    /**
     * Closes current window and switch to the specified
     *
     * @param handle  - the handle of the window you want to switch to
     * @param usingJS - whether the window should be closed using javascript
     * @throws Exception
     */
    public void closeCurrentWindowAndSwitchToSpecified(String handle, boolean usingJS) throws Exception {
        int winHandles = getWindowCount();
        if (usingJS) {
            String js = "Object.defineProperty(BeforeUnloadEvent.prototype, 'returnValue', " +
                    "{ get:function(){}, set:function(){} });";
            ((JavascriptExecutor) this.driver).executeScript(js);
            ((JavascriptExecutor) this.driver).executeScript("window.close();");
        } else
            close();
        waitUntilWindowHandlesIsChanged(winHandles, short_timeout);
        switchToWindow(handle);
    }

    /**
     * Closes some opened windows except the specified number. The method leaves any windows open in Safari
     *
     * @param numberToStayOpen - specifies a number of windows, which will remain open
     * @return
     * @throws Exception
     * @deprecated - use closeWindowsExceptSpecified(boolean usingJS, String... handles)
     */
    public BaseWebPage closeWindowsExceptSpecified(int numberToStayOpen, boolean usingJS) throws Exception {
        if (numberToStayOpen >= 1) {
            int winHandles = getWindowCount();
            for (int i = winHandles - 1; i >= numberToStayOpen; i--) {
                switchToWindow(i);
                if (getCurrentUrl().equals("about:blank"))
                    close();
                else if (usingJS)
                    ((JavascriptExecutor) this.driver).executeScript("window.close();");
                else
                    close();
            }
            switchToWindow(numberToStayOpen - 1);
            waitUntilWindowHandlesIs(numberToStayOpen, 2 * short_timeout);
        }
        return this;
    }

    /**
     * Closes some opened windows except the specified number. The method leaves any windows open in Safari
     *
     * @param numberToStayOpen - specifies a number of windows, which will remain open
     * @return
     * @throws Exception
     * @deprecated - use closeWindowsExceptSpecified(boolean usingJS, String... handles)
     */
    public BaseWebPage closeWindowsExceptSpecified(int numberToStayOpen) throws Exception {
        return closeWindowsExceptSpecified(numberToStayOpen, true);
    }

    /**
     * Closes some opened windows except the specified
     *
     * @param handles - specifies handles of the windows, which will remain open
     * @return
     * @throws Exception
     */
    public BaseWebPage closeWindowsExceptSpecified(boolean usingJS, String... handles) throws Exception {
        String[] winHandles = driver.getWindowHandles().toArray(new String[0]);
        List<String> handlesList = Arrays.asList(handles);
        for (int i = 0; i < winHandles.length; i++)
            if (!handlesList.contains(winHandles[i])) {
                driver.switchTo().window(winHandles[i]);
                if (getCurrentUrl().equals("about:blank"))
                    close();
                else if (usingJS)
                    ((JavascriptExecutor) this.driver).executeScript("window.close();");
                else
                    close();
            }
        driver.switchTo().window(handles[handles.length - 1]);
        waitUntilWindowHandlesIs(handles.length, 2 * short_timeout);
        return this;
    }

    /**
     * Closes some opened windows except the specified
     *
     * @param windows - specifies windows, which will remain open
     * @return
     * @throws Exception
     */
    public BaseWebPage closeWindowsExceptSpecified(boolean usingJS, BaseWebPage... windows) throws Exception {
        return closeWindowsExceptSpecified(
                usingJS, Arrays.stream(windows).map(BaseWebPage::getInitialHandle).toArray(String[]::new));
    }

    /**
     * Returns initial handle
     *
     * @return String
     */
    public String getInitialHandle() {
        return initialHandle;
    }

    /**
     * Emulates closing a window
     * If Alert message is present, then the current window won't close
     * If there is no Alert message, then the current window will close
     *
     * @throws Exception
     */
    public void emulateWindowClosing() throws Exception {
        int windowsCount = getWindowCount();
        jsExecute("return window.close(false);");
        waitUntilWindowHandlesIsChanged(windowsCount, tiny_timeout);
        Set winHandles = driver.getWindowHandles();
        if (!winHandles.contains(this.initialHandle))
            driver.switchTo().window((String) winHandles.iterator().next());
    }

    /**
     * Zoom in or zoom out the page using javascript
     *
     * @param percent
     * @throws InterruptedException
     */
    public void setPageZoomJs(int percent) throws InterruptedException {
        jsExecute("document.body.style.zoom='" + percent + "%'");
    }
}

