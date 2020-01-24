package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.utils.XmlUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.NodeList;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public abstract class BaseWidget extends BaseWrapper {

    protected WebElement webElement;

    void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    // ------ CONSTRUCTORS ------ //
    public BaseWidget(WebDriver driver) {
        super(driver);
    }

    public BaseWidget(WebDriver driver, CustomLocator locator) {
        super(driver);
        this.locator = locator;
        initElements(locator);
    }

    /**
     * Is stale element reference?
     *
     * @return true - if an element isn't presented in the DOM. If an element is presented in the DOM returns false
     */
    boolean isStaleReference() {
        try {
            // Calling any method forces a staleness check
            webElement.isEnabled();
            return false;
        } catch (WebDriverException expected) {
            return true;
        }
    }

    /**
     * Finds an element if it has not already been found
     */
    protected void initialWebElementIfNeeded() {
        initialWebElementIfNeeded(timeout);
    }

    protected void initialWebElementIfNeeded(int timeout) {
        try {
            if (locator != null) {
                if (webElement == null) {
                    initWebElement(timeout);
                } else {
                    if (!isCacheLookup() && isStaleReference())
                        initWebElement(timeout);
                }
            }
        } catch (Exception err) {
            Log.error("Element " + (getMetaName() != null ? getMetaName() : "") + " not found. " + err.getMessage());
            throw err;
        }
    }

    /**
     * Finds an web element
     */
    protected void initWebElement(int timeout) {
        webElement = findElement(locator, timeout);
    }

    protected void initWebElement() {
        webElement = findElement(locator);
    }

    /**
     * Find Child web element with default timeout
     *
     * @param by - locator
     * @return WebElement
     */
    protected WebElement findChildWebElement(By by) {
        initialWebElementIfNeeded();
        return webElement.findElement(by);
    }

    /**
     * Find Child Element with default timeout
     *
     * @param xpath - Xpath
     * @return Element
     */
    public Element findChildElement(String xpath) throws Exception {
        return findChildElement(xpath, Element.class);
    }

    public <T extends BaseWidget> T findChildElement(String xpath, Class<? extends BaseWidget> clazz) throws Exception {
        if (xpath.startsWith("."))
            xpath = xpath.replaceFirst(".", "");
        return (T) clazz.getConstructor(WebDriver.class, CustomLocator.class)
                .newInstance(driver, new CustomLocator(By.xpath(getXpath() + xpath)));
    }

    public <T extends BaseWidget> ElementList<T> findChildElements(String xpath, Class<? extends BaseWidget> clazz) throws Exception {
        if (xpath.startsWith("."))
            xpath = xpath.replaceFirst(".", "");
        CustomLocator locator = new CustomLocator(By.xpath(getXpath() + xpath));
        locator.setCacheLookup(true);
        return new ElementList<>(driver, locator, clazz);
    }

    @Override
    protected CustomLocator buildLocator(String str, String metaName) {
        CustomLocator locator;
        if (str.startsWith("."))
            locator = new CustomLocator(
                    this.getXpath() + By.xpath(str.replaceFirst("\\.", "")), metaName);
        else
            locator = super.buildLocator(str, metaName);
        return locator;
    }

    /**
     * Get value of attribute from Page Source
     * @param pageSource - xml of the page
     * @param attribute - name of attribute
     * @return value of the attribute
     */
    protected String getAttributeValueFromPageSource(String pageSource, String attribute, String xpath) {
        NodeList nodeList = null;
        try {
            nodeList = XmlUtil.getXpathExpressionResultFromXml(pageSource, xpath);
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
        if (nodeList == null || nodeList.getLength() == 0)
            return null;
        return nodeList.item(0).getAttributes().getNamedItem(attribute).getNodeValue();
    }

    protected String getAttributeValueFromPageSource(String pageSource, String attribute) {
        return getAttributeValueFromPageSource(pageSource, attribute, getXpath());
    }

    public boolean isPresent() {
        this.setImplicitWait(0);
        boolean isPresent = findElements(locator).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    public boolean isPresent(int timeout) {
        this.setImplicitWait(timeout);
        boolean isPresent = findElements(locator).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    /**
     * Wait for the web element is visible
     *
     * @param timeout
     * @throws Exception
     */
    private void waitForVisibilityAndSearchAgain(int timeout, Duration sleepTimeout, boolean isSearchingAgain) {
        WebDriverWait wait = new WebDriverWait(this.driver, timeout);
        if (sleepTimeout != null)
            wait.pollingEvery(sleepTimeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> isVisible());
        } catch (StaleElementReferenceException e) {
            if (isSearchingAgain) {
                webElement = null;
                if (locator != null)
                    locator.setCacheLookup(false);
                initialWebElementIfNeeded();
                waitForVisibilityAndSearchAgain(timeout, sleepTimeout, false);
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            Log.warn(String.format("waitForVisibilityAndSearchAgain for " + getMetaName() + " failed (tried for %d second(s))", timeout));
        }
    }

    public boolean isVisible(String pageSource) {
        // Only for Android
        return "true".equals(
                getAttributeValueFromPageSource(pageSource, "displayed"));
    }

    public boolean isVisible() {
        WebElement we = null;
        if (locator != null) {
            this.setImplicitWait(0);
            try {
                we = findElement(locator);
            } catch (NoSuchElementException err) {
                // Element is not present
            }
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
            if (we == null)
                return false;
            webElement = we;
        } else
            we = webElement;
        try {
            return we.isDisplayed();
        } catch (StaleElementReferenceException err) {
            return false;
        }
    }

    public boolean isVisible(int timeout) {
        this.setImplicitWait(timeout);
        List<WebElement> webElementList = findElements(locator);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return webElementList.size() > 0 && webElementList.get(0).isDisplayed();
    }

    public void waitForVisibility() {
        waitForVisibility(timeout);
    }

    public void waitForVisibility(int timeout) {
        waitForVisibilityAndSearchAgain(timeout, null, true);
    }

    public void waitForVisibility(int timeout, Duration sleepTimeout) {
        waitForVisibilityAndSearchAgain(timeout, sleepTimeout, true);
    }

    public void waitForInvisibility() {
        waitForInvisibility(timeout);
    }

    public void waitForInvisibility(int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> {
                try {
                    return !isVisible();
                } catch (StaleElementReferenceException elRefErr) {
                    return false;
                }
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            Log.warn(String.format("waitForInvisibility failed (tried for %d second(s))", timeout));
        }
    }

    public Rectangle getRectangle() {
        initialWebElementIfNeeded();
        Rectangle rect;
        try {
            rect = new Rectangle(webElement.getLocation(), webElement.getSize());
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + getMetaName() + " Method: getRectangle(). Error: ElementNotInteractableException");
            rect = getRectangleJs();
        }
        return rect;
    }

    public Point getLocation() {
        initialWebElementIfNeeded();
        try {
            return webElement.getLocation();
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + getMetaName() + " Method: getLocation(). Error: ElementNotInteractableException");
            Rectangle rect = getRectangleJs();
            return new Point(rect.x, rect.y);
        }
    }

    public Dimension getSize() {
        initialWebElementIfNeeded();
        return webElement.getSize();
    }

    public int getHeight() {
        initialWebElementIfNeeded();
        try {
            return webElement.getSize().getHeight();
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + getMetaName() + " Method: getHeight(). Error: ElementNotInteractableException");
            return getRectangleJs().getHeight();
        }
    }

    public int getWidth() {
        initialWebElementIfNeeded();
        try {
            return webElement.getSize().getWidth();
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + getMetaName() + " Method: getWidth(). Error: ElementNotInteractableException");
            return getRectangleJs().getWidth();
        }
    }

    private int getIntFromObject(Object obj) {
        if (obj instanceof Double)
            return (int) Math.round((Double) obj);
        if (obj instanceof Long) {
            return Math.round((Long) obj);
        }
        throw new RuntimeException("Unknown type");
    }

    public Rectangle getRectangleJs() {
        return getRectangleJs(3);
    }

    /**
     * Gets the rectangle of the web element using javascript
     *
     * @param attemptsNumber - defines additional attempts to get the rectangle of the element
     * @return Rectangle
     */
    private Rectangle getRectangleJs(int attemptsNumber) {
        try {
            initialWebElementIfNeeded();
            String js = "return arguments[0].getBoundingClientRect()";
            Map<String, Object> mapRect = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(js, webElement);
            String keyX = /*isEdge()*/false ? "left" : "x";
            String keyY = /*isEdge()*/false ? "top" : "y";
            String keyWidth = "width";
            String keyHeight = "height";
            int x = getIntFromObject(mapRect.get(keyX));
            int y = getIntFromObject(mapRect.get(keyY));
            int width = getIntFromObject(mapRect.get(keyWidth));
            int height = getIntFromObject(mapRect.get(keyHeight));
            return new Rectangle(x, y, height, width);
        } catch (StaleElementReferenceException e) {
            if (attemptsNumber > 0) {
                return getRectangleJs(attemptsNumber - 1);
            } else
                throw new RuntimeException("Element: " + getMetaName()
                        + ". Method: getRectangleJs() - " + e.getMessage());
        }
    }


}
