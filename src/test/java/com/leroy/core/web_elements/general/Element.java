package com.leroy.core.web_elements.general;

import com.leroy.constants.Fonts;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.util.Strings;

import java.util.List;
import java.util.Map;

public class Element extends BaseElement {

    protected WebElement webElement;
    private String metaName;

    // ------ CONSTRUCTORS ------ //
    public Element(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public Element(WebDriver driver, By by) {
        super(driver);
        this.locator = new CustomLocator(by);
        initElements(this.locator);
    }

    public Element(WebDriver driver, CustomLocator locator, String metaName) {
        super(driver);
        this.locator = locator;
        this.metaName = metaName;
        initElements(this.locator);
    }

    public Element(WebDriver driver, WebElement we) {
        super(driver);
        this.webElement = we;
        this.locator = new CustomLocator(By.xpath(getAbsoluteXPath()));
        initElements(locator);
    }

   /* public Element(WebDriver driver, By locator, String metaName) {
        this.driver = driver;
        this.locator = new CustomLocator(locator);
        this.metaName = metaName;
        initElements(this.locator);
    }*/

    public Element(WebDriver driver, WebElement we, CustomLocator locator) {
        this(driver, locator);
        this.webElement = we;
    }

    public WebElement getWebElement() {
        initialWebElementIfNeeded();
        return this.webElement;
    }

    /**
     * Execute js script
     *
     * @param jsScript - javaScript
     */
    protected Object executeScript(String jsScript) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(jsScript, webElement);
    }

    /**
     * Get name of Element
     */
    public String getMetaName() {
        return metaName;
    }

    /**
     * Set the name of the Element
     *
     * @param metaName
     */
    protected Element setMetaName(String metaName) {
        this.metaName = metaName;
        return this;
    }

    /**
     * Get xpath of the element
     *
     * @return String
     */
    @Override
    public String getXpath() {
        if (locator == null)
            return getAbsoluteXPath();
        else if (locator.getAccessibilityId() != null && !locator.getAccessibilityId().isEmpty()) {
            return XpathUtil.getXpathByAccessibilityId(locator.getAccessibilityId());
        } else {
            return super.getXpath();
        }
    }

    /**
     * Get absolute xpath of the webElement
     *
     * @return
     */
    private String getAbsoluteXPath() {
        String jsScript = "function absoluteXPath(element) {" +
                "var comp, comps = [];" +
                "var parent = null;" +
                "var xpath = '';" +
                "var getPos = function(element) {" +
                "var position = 1, curNode;" +
                "if (element.nodeType == Node.ATTRIBUTE_NODE) {" +
                "return null;" +
                "}" +
                "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling) {" +
                "if (curNode.nodeName == element.nodeName) {" +
                "++position;" +
                "}" +
                "}" +
                "return position;" +
                "};" +

                "if (element instanceof Document) {" +
                "return '/';" +
                "}" +

                "for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {" +
                "comp = comps[comps.length] = {};" +
                "switch (element.nodeType) {" +
                "case Node.TEXT_NODE:" +
                "comp.name = 'text()';" +
                "break;" +
                "case Node.ATTRIBUTE_NODE:" +
                "comp.name = '@' + element.nodeName;" +
                "break;" +
                "case Node.PROCESSING_INSTRUCTION_NODE:" +
                "comp.name = 'processing-instruction()';" +
                "break;" +
                "case Node.COMMENT_NODE:" +
                "comp.name = 'comment()';" +
                "break;" +
                "case Node.ELEMENT_NODE:" +
                "comp.name = element.nodeName;" +
                "break;" +
                "}" +
                "comp.position = getPos(element);" +
                "}" +

                "for (var i = comps.length - 1; i >= 0; i--) {" +
                "comp = comps[i];" +
                "xpath += '/' + comp.name.toLowerCase();" +
                "if (comp.position !== null) {" +
                "xpath += '[' + comp.position + ']';" +
                "}" +
                "}" +

                "return xpath;" +

                "} return absoluteXPath(arguments[0]);";
        String rawXPath;
        try {
            rawXPath = executeScript(jsScript).toString();
        } catch (StaleElementReferenceException err) {
            initialWebElementIfNeeded();
            rawXPath = executeScript(jsScript).toString();
        }
        return rawXPath
                .replaceAll("/svg\\[", "/*[name() = 'svg'][")
                .replaceAll("/g\\[", "/*[name() = 'g'][")
                .replaceAll("/path\\[", "/*[name() = 'path'][")
                .replaceAll("/rect\\[", "/*[name() = 'rect'][")
                .replaceAll("/text\\[", "/*[name() = 'text'][")
                .replaceAll("/ellipse\\[", "/*[name() = 'ellipse'][")
                .replaceAll("/line\\[", "/*[name() = 'line'][")
                .replaceAll("/polyline\\[", "/*[name() = 'polyline'][")
                .replaceAll("/image\\[", "/*[name() = 'image'][");
    }

    /**
     * Wait for the web element is visible
     *
     * @param timeout
     * @throws Exception
     */
    private void waitForVisibilityAndSearchAgain(int timeout, boolean isSearchingAgain) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> isVisible());
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            if (isSearchingAgain) {
                webElement = null;
                if (locator != null)
                    locator.setCacheLookup(false);
                initialWebElementIfNeeded();
                waitForVisibilityAndSearchAgain(timeout, false);
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            Log.warn(String.format("waitForVisibilityAndSearchAgain failed (tried for %d second(s))", timeout));
        }
    }

    // ------ PROTECTED METHODS ------ //

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
     * Find Child element with default timeout
     *
     * @param by - locator
     * @return WebElement
     */
    protected WebElement findChildElement(By by) {
        initialWebElementIfNeeded();
        return webElement.findElement(by);
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

    // ------ PUBLIC METHODS ------ //

    /**
     * Find Child elements
     *
     * @param by - locator
     * @return WebElement
     */
    public List<WebElement> findChildElements(By by) {
        return findChildElements(by, 1);
    }

    /**
     * Find Child elements
     *
     * @param by      - locator
     * @param timeout - timeout
     * @return WebElement
     */
    public List<WebElement> findChildElements(By by, int timeout) {
        initialWebElementIfNeeded();
        this.setImplicitWait(timeout);
        List<WebElement> webElements = webElement.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return webElements;
    }

    public void click() {
        initialWebElementIfNeeded();
        simpleClick(0);
    }

    public void doubleClick() {
        initialWebElementIfNeeded();
        simpleClick(0);
        simpleClick(0);
    }

    private void simpleClick(int additionalAttemptNum) {
        try {
            webElement.click();
        } catch (StaleElementReferenceException err) {
            Log.warn("simpleClick() failed. Err: " + err.getMessage());
            if (additionalAttemptNum > 0) {
                initialWebElementIfNeeded();
                simpleClick(additionalAttemptNum - 1);
            } else
                throw err;
        }
    }

    public void click(int timeout) {
        try {
            waitForClickability(timeout, 2);
            simpleClick(1);
        } catch (Exception err) {
            Log.error("click(): " + err.getMessage());
            throw err;
        }
    }

    private void waitForClickability(int timeout, int attempt) {
        initialWebElementIfNeeded();
        String s = "";
        try {
            new WebDriverWait(this.driver, timeout).until(
                    ExpectedConditions.elementToBeClickable(this.webElement));
        } catch (TimeoutException err) {
            Log.warn(String.format("waitForClickability failed (tried for %d second(s))", timeout));
        } catch (StaleElementReferenceException err) {
            if (attempt > 0)
                waitForClickability(1, attempt - 1);
        }
    }

    public void clickJS() {
        initialWebElementIfNeeded();
        try {
            ((JavascriptExecutor) this.driver).executeScript(
                    "var evt = document.createEvent('MouseEvents');" +
                            "evt.initMouseEvent(" +
                            "'click', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                            "arguments[0].dispatchEvent(evt);", new Object[]{this.webElement});
        } catch (Exception err) {
            Log.error("clickJS(): " + err.getMessage());
            throw err;
        }
    }

    public void clickJS(int screenX, int screenY, int clientX, int clientY) {
        initialWebElementIfNeeded();
        try {
            ((JavascriptExecutor) this.driver).executeScript(
                    "var evt = document.createEvent('MouseEvents');" +
                            "evt.initMouseEvent(" +
                            "'click', true, true, window, 0, " +
                            screenX + ", " +
                            screenY + ", " +
                            clientX + ", " +
                            clientY + ", " +
                            " false, false, false, false, 0, null);" +
                            "arguments[0].dispatchEvent(evt);", new Object[]{this.webElement});
        } catch (Exception err) {
            Log.error("clickJS(): " + err.getMessage());
            throw err;
        }
    }

    public void clickWithOffset(int xOffset, int yOffset) {
        initialWebElementIfNeeded();
        try {
            Actions builder = new Actions(driver);
            builder.moveToElement(webElement, xOffset, yOffset).click().build().perform();
        } catch (Exception err) {
            Log.error("click(): " + err.getMessage());
            throw err;
        }
    }

    public void rightClick() {
        initialWebElementIfNeeded();
        try {
            //new TouchActions(driver).longPress(this.webElement).perform();
            //new TouchAction((IOSDriver) driver).longPress(
            //                        PointOption.point(((MobileElement) webElement).getCenter().x, ((MobileElement) webElement).getCenter().y)
            //                ).release().perform();
//            if (isMobile()) {
//                rightClickJS();
//            } else {
            try {
                new Actions(this.driver).contextClick(webElement).perform();
            } catch (ElementNotInteractableException err) {
                Log.warn("Element: " + getMetaName() + " Method: rightClick(). Error: ElementNotInteractableException");
                rightClickJS();
            }
            //}
        } catch (Exception err) {
            Log.error("rightClick(): " + err.getMessage());
            throw err;
        }
    }

    public void mouseMoveJS(int xOffset, int yOffset) {
        initialWebElementIfNeeded();
        try {
            xOffset = xOffset < 0 ? 0 : xOffset;
            yOffset = yOffset < 0 ? 0 : yOffset;
            Rectangle rect = getRectangleJs();
            int clientX = rect.x + xOffset <= rect.x + rect.width ? rect.x + xOffset : rect.x + rect.width;
            int clientY = rect.y + yOffset <= rect.y + rect.height ? rect.y + yOffset : rect.y + rect.height;
            ((JavascriptExecutor) this.driver).executeScript(
                    "var evt = document.createEvent('MouseEvents');" +
                            "evt.initMouseEvent(" +
                            "'mousemove', true, true, window, 0, 0, 0, " +
                            clientX + "," +
                            clientY +
                            ", false, false, false, false, 0, null);" +
                            "arguments[0].dispatchEvent(evt);", new Object[]{this.webElement});
        } catch (Exception err) {
            Log.error("mouseMoveJS(): " + err.getMessage());
            throw err;
        }
    }

    public void rightClickJS() {
        initialWebElementIfNeeded();
        try {
            Rectangle rect = getRectangleJs();
            ((JavascriptExecutor) this.driver).executeScript(
                    "var evt = document.createEvent('MouseEvents');" +
                            "evt.initMouseEvent(" +
                            "'contextmenu', true, true, window, 0, 0, 0, " +
                            (rect.x + rect.width / 2) + "," +
                            (rect.y + rect.height / 2) +
                            ", false, false, false, false, 2, null);" +
                            "arguments[0].dispatchEvent(evt);", new Object[]{this.webElement});
        } catch (Exception err) {
            Log.error("rightClickJS(): " + err.getMessage());
            throw err;
        }
    }

    /**
     * return tagname for the element
     */
    public String getTagName() {
        return getTagName(3);
    }

    /**
     * Returns the tag name for the element
     *
     * @param attemptsNumber - defines additional attempts to get tag name of the element
     */
    private String getTagName(int attemptsNumber) {
        initialWebElementIfNeeded();
        try {
            return webElement.getTagName();
        } catch (StaleElementReferenceException e) {
            if (attemptsNumber > 0)
                return getTagName(attemptsNumber - 1);
            else
                throw new RuntimeException("Element: " + getMetaName() + ". Method: Element.getTagName() - " +
                        e.getMessage());
        }
    }

    private String getText(boolean selfText, int attemptsNumber) {
        try {
            initialWebElementIfNeeded(short_timeout);
            if (selfText) {
                return ((JavascriptExecutor) driver).executeScript(
                        "var parent = arguments[0];" +
                                "var child = parent.firstChild;" +
                                "var ret = \"\";" +
                                "while(child) {" +
                                "    if (child.nodeType === Node.TEXT_NODE)" +
                                "        ret += child.textContent;" +
                                "    child = child.nextSibling;" +
                                "}" +
                                "return ret;", this.webElement).toString();
            } else {
                // replaceAll is needed for Edge browser (specific situations)
                return webElement.getText();
            }
        } catch (StaleElementReferenceException err) {
            Log.debug("Method: getText(). StaleElementReferenceException: " + err.getMessage());
            if (attemptsNumber > 0) {
                if (locator != null)
                    locator.setCacheLookup(false);
                return getText(selfText, attemptsNumber - 1);
            }
            return null;
        } catch (NoSuchElementException err) {
            Log.warn("Method: getText(). NoSuchElementException: " + err.getMessage());
            if (attemptsNumber > 0)
                return getText(selfText, attemptsNumber - 1);
            return null;
        } catch (Exception err) {
            Log.error("Method: getText()");
            Log.error("Exception: " + err.getMessage());
            return null;
        }
    }

    /**
     * Get text from web element
     *
     * @return String
     */
    public String getText() {
        return getText(false);
    }

    /**
     * Get text from web element
     *
     * @param selfText - only text of an element without including child elements text
     * @return String
     */
    public String getText(boolean selfText) {
        return getText(selfText, 1);
    }

    public String getFontFamily() {
        return getCssValue("font-family")
                .replaceAll("\"", "").replaceAll(",\\s", ",");
    }

    public Fonts getFontWeight() {
        String cssVal = getCssValue("font-weight");
//        if (isIos() || isSafari()) {
//            switch (cssVal) {
//                case "bold":
//                    return FontsKt.WEIGHT_BOLD;
//                case "normal":
//                    return FontsKt.WEIGHT_NORMAL;
//            }
//        } else {
        switch (cssVal) {
            case "700":
                return Fonts.WEIGHT_BOLD;
            case "400":
                return Fonts.WEIGHT_NORMAL;
        }
        // }
        if (Strings.isNullOrEmpty(cssVal))
            return Fonts.WEIGHT_NORMAL;
        return null;
    }

    public String getFontStyle() {
        return getCssValue("font-style");
    }

    public String getFontSize() {
        return getCssValue("font-size").replace("px", "");
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

    public boolean isVisible() {
        WebElement we;
        if (locator != null) {
            this.setImplicitWait(0);
            List<WebElement> webElementList = findElements(locator);
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
            if (webElementList.size() <= 0)
                return false;
            we = webElementList.get(0);
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
        waitForVisibility(short_timeout);
    }

    public void waitForVisibility(int timeout) {
        waitForVisibilityAndSearchAgain(timeout, true);
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

    public Element hoverOver() {

        return hoverOver(false);
    }

    public Element hoverOver(boolean isJs) {
        if (isJs) {
            return hoverOverJS();
        } else {
            initialWebElementIfNeeded();
            new Actions(this.driver).moveToElement(webElement).perform();
            return this;
        }
    }

    public Element hoverOver(int xOffset, int yOffset) {
        initialWebElementIfNeeded();
        new Actions(this.driver).moveToElement(webElement, xOffset, yOffset).perform();
        return this;
    }

    private Element hoverOverJS() {
        initialWebElementIfNeeded();
        Rectangle rect = getRectangleJs();
        String mouseOverScript =
                "var evt = document.createEvent('MouseEvents');" +
                        "evt.initMouseEvent(" +
                        "'mouseover', true, true, window, 0, 0, 0, " +
                        (rect.x + rect.width / 2) + ", " +
                        (rect.y + rect.height / 2) +
                        ", false, false, false, false, 0, null);" +
                        "arguments[0].dispatchEvent(evt);";
        executeScript(mouseOverScript);
        return this;
    }

    public Element hoverOverJS(int screenX, int screenY, int clientX, int clientY) {
        initialWebElementIfNeeded();
        String mouseOverScript =
                "var evt = document.createEvent('MouseEvents');" +
                        "evt.initMouseEvent(" +
                        "'mouseover', true, true, window, 0, " +
                        screenX + ", " +
                        screenY + ", " +
                        clientX + ", " +
                        clientY +
                        ", false, false, false, false, 0, null);" +
                        "arguments[0].dispatchEvent(evt);";
        executeScript(mouseOverScript);
        return this;
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

    public String getCssValue(String cssAttribute) {
        return getCssValue(cssAttribute, 3);
    }

    private String getCssValue(String cssAttribute, int attemptsNumber) {
        try {
            initialWebElementIfNeeded();
            return webElement.getCssValue(cssAttribute);
        } catch (StaleElementReferenceException e) {
            if (attemptsNumber > 0) {
                initialWebElementIfNeeded();
                return getCssValue(cssAttribute, attemptsNumber - 1);
            } else
                throw new RuntimeException("Element: " + getMetaName() + ". Method: Element.getCssValue() - " + e.getMessage());
        }
    }

    public String getAttribute(String attribute) {
        return getAttribute(attribute, 3);
    }

    /**
     * Get web element's attribute
     *
     * @param attribute
     * @param attemptsNumber - defines additional attempts to getting the attribute of the element
     * @return String
     */
    private String getAttribute(String attribute, int attemptsNumber) {
        initialWebElementIfNeeded();
        try {
            return webElement.getAttribute(attribute);
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            if (attemptsNumber > 0) {
                return getAttribute(attribute, attemptsNumber - 1);
            } else
                throw new RuntimeException("Element: " + getMetaName() + ". Method: Element.getAttribute() - " + e.getMessage());
        }
    }

    /**
     * Is enabled?
     *
     * @return true/false
     */
    public boolean isEnabled() {
        initialWebElementIfNeeded();
        return webElement.isEnabled();
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

    public int getY1Js() {
        return getRectangleJs().y;
    }

    public int getY2Js() {
        Rectangle r = getRectangleJs();
        return r.y + r.height;
    }

    public int getX1Js() {
        return getRectangleJs().x;
    }

    public int getX2Js() {
        Rectangle r = getRectangleJs();
        return r.x + r.width;
    }

    public int getWidthJs() {
        return getRectangleJs().width;
    }

    public int getHeightJs() {
        return getRectangleJs().height;
    }

    public boolean waitUntilTextIsDifferent() {
        return waitUntilTextIsDifferent(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
    }

    public boolean waitUntilTextIsDifferent(int timeout) {
        return waitUntilTextIsDifferent(null, timeout);
    }

    public boolean waitUntilTextIsDifferent(String previousText, int timeout) {
        initialWebElementIfNeeded();
        String oldText = previousText == null ? getText() : previousText;
        try {
            (new WebDriverWait(this.driver, timeout)).until((driver) -> {
                try {
                    String actualText = getText();
                    return !oldText.equals(actualText) && !"".equals(actualText);
                } catch (Exception var4) {
                    return false;
                }
            });
        } catch (TimeoutException err) {
            Log.warn(String.format(
                    "Expected condition failed: waitAndGetUntilTextIsDifferent " +
                            "(tried for %d second(s)). Element: " + getMetaName(), timeout));
        }
        return !oldText.equals(getText());
    }

    public void waitUntilTextIsEqualTo(String referenceText, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> this.getText().equals(referenceText));
        } catch (TimeoutException e) {
            Log.warn(String.format(
                    "Method: waitUntilTextIsEqualTo() - Text isn't equal to '%s' (tried for %d second(s))",
                    referenceText, timeout));
        }
    }

    public void waitUntilTextIsEqualTo(String referenceText) {
        waitUntilTextIsEqualTo(referenceText, short_timeout);
    }

    public void waitUntilTextContains(String referenceText, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> this.getText().contains(referenceText));
        } catch (TimeoutException e) {
            Log.warn(String.format(
                    "Method: waitUntilTextContains() - the text doesn't contain a specified (tried for %d second(s))",
                    timeout));
        }
    }

    public void waitUntilTextContains(String referenceText) {
        waitUntilTextContains(referenceText, short_timeout);
    }

    /**
     * get Rotate attribute value for the element
     *
     * @return String
     */
    public String getRotate() {
        initialWebElementIfNeeded();
        String rotate = StringUtils.substringBetween(webElement.getAttribute("transform"), "rotate(", ")");
        if (rotate == null)
            return "0";
        return rotate.split(" ")[0].replaceAll("\\+", "");
    }

    public void scrollTo(String block, String inline) {
        initialWebElementIfNeeded();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: \"instant\", block: \"" + block + "\", inline: \"" + inline + "\"})",
                webElement);
    }

    public void scrollTo(boolean align) {
        initialWebElementIfNeeded();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(" + align + ");", webElement);
    }

    public void scrollTo() {
        scrollTo("center", "center");
    }

    public void dragAndDrop(int xOffset, int yOffset) {
        initialWebElementIfNeeded();
        Actions actions = new Actions(driver);
        actions.dragAndDropBy(this.webElement, xOffset, yOffset).build().perform();
    }

    public void dragAndDrop(Element target) {
        initialWebElementIfNeeded();
        Actions actions = new Actions(driver);
        actions.dragAndDrop(this.webElement, target.getWebElement()).build().perform();
    }

    public Color getBorderColor() {
        return Color.fromString(getCssValue("border-color"));
    }

    public Color getColor() {
        return Color.fromString(getCssValue("color"));
    }

    /**
     * Gets CSS value 'background-color' of the element
     *
     * @return Color
     */
    public Color getBackgroundColor() {
        return Color.fromString(getCssValue("background-color"));
    }

    /**
     * Gets attribute value 'innerText' of the element (with formatted chars)
     *
     * @return String
     */
    public String getInnerText() {
        initialWebElementIfNeeded();
        return getAttribute("innerText");
    }

    /**
     * check if element has vertical scrollbar
     *
     * @return boolean
     */
    public boolean isElementHasVerticalScrollbar() {
        initialWebElementIfNeeded();
        String overflow = webElement.getCssValue("overflow");
        String overflowY = webElement.getCssValue("overflow-y");
        return overflowY.equals("scroll") || overflowY.equals("auto") ||
                overflow.equals("scroll") || overflow.equals("auto");
    }

    /**
     * Wait for the text of the element isn't equal to the specified
     *
     * @param initialText
     */
    public void waitForTextIsNotEqual(String initialText) {
        WebDriverWait wait = new WebDriverWait(this.driver, (long) short_timeout);
        try {
            wait.until((ExpectedCondition<Boolean>) driverObject -> {
                try {
                    return !initialText.equals(getText());
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            Log.warn(String.format(
                    "Expected condition failed: waitForTextIsNotEqual (tried for %d second(s))", short_timeout));
        }
    }

    /**
     * check if element has scrollbar
     *
     * @param bHorizontal true if check horizontal, false - if check vertical
     * @return boolean
     */
    public boolean isScrollbarVisible(boolean bHorizontal) throws Exception {
        initialWebElementIfNeeded();
        String overflow = webElement.getCssValue("overflow");
        String overflowOriental = webElement.getCssValue(bHorizontal ? "overflow-x" : "overflow-y");
        if (overflowOriental.equals("scroll") || overflowOriental.equals("auto") ||
                overflow.equals("scroll") || overflow.equals("auto")) {
            try {
                String jsScript = "var elem = document.evaluate(\"" + getXpath() +
                        "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                        "return " +
                        (bHorizontal ?
                                "elem.scrollWidth > elem.clientWidth;" : "elem.scrollHeight > elem.clientHeight;");
                return Boolean.parseBoolean(executeScript(jsScript).toString());
            } catch (Exception ex) {
                Log.error("method: isScrollBarVisible(" + bHorizontal + ")" +
                        " - javascript for element scrollbar checking is failed");
                throw new Exception("Element: " + getMetaName() + ". Method: isScrollbarVisible. Error: " + ex.getMessage());
            }
        } else
            return false;
    }

    /**
     * check if text is clipped and clipped part is turn in dots
     *
     * @return boolean
     */
    public boolean isTextWillBeClippedByDots() {
        return getCssValue("text-overflow").equals("ellipsis");
    }

    /**
     * check if text is clipped
     *
     * @return boolean
     */
    public boolean isTextWillBeClippedWithoutDots() {
        return getCssValue("text-overflow").equals("clip");
    }

    /**
     * Scroll the element's content to the bottom
     */
    public void scrollContentToTheBottomJs() {
        initialWebElementIfNeeded();
        executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;");
        WebDriverWait wait = new WebDriverWait(this.driver, (long) tiny_timeout);
        try {  //we need to wait for the element's content to be scrolled down, especially for Edge
            wait.until((ExpectedCondition<Boolean>) driverObject -> isElementContentScrolledToBottom());
        } catch (TimeoutException e) {
            Log.warn(String.format("scrollContentToTheBottomJs() - element's content wasn't scrolled to the bottom - " +
                    "waited for %d second(s))", tiny_timeout));
        }
    }

    /**
     * Scroll the element's content to the right
     */
    public void scrollContentToTheRightJs() {
        initialWebElementIfNeeded();
        executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;");
        WebDriverWait wait = new WebDriverWait(this.driver, (long) tiny_timeout);
        try {  //we need to wait for the element's content to be scrolled to the right, especially for Edge
            wait.until((ExpectedCondition<Boolean>) driverObject -> isElementContentScrolledToRight());
        } catch (TimeoutException e) {
            Log.warn(String.format("scrollContentToTheRightJs() - element's content wasn't scrolled to the right - " +
                    "waited for %d second(s))", tiny_timeout));
        }
    }

    /**
     * Get the number of pixels that an element's content is scrolled vertically.
     */
    public int getContentVerticalScrollPositionJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].scrollTop")).intValue();
    }

    /**
     * Get the number of pixels that an element's content is scrolled horizontally.
     */
    public int getContentHorizontalScrollPositionJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].scrollLeft")).intValue();
    }

    /**
     * Get a client height of the element.
     * Client height can be calculated as: CSS height + CSS padding - height of horizontal scrollbar (if present).
     * For more information see https://developer.mozilla.org/en-US/docs/Web/API/Element/clientHeight
     *
     * @return int
     */
    public int getClientHeightJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].clientHeight")).intValue();
    }

    /**
     * Get a client width of the element.
     * Client width includes padding but excludes borders, margins, and vertical scrollbars (if present).
     * For more information see https://developer.mozilla.org/en-US/docs/Web/API/Element/clientWidth
     *
     * @return int
     */
    public int getClientWidthJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].clientWidth")).intValue();
    }

    /**
     * Get the height of an element's content, including content not visible on the screen due to overflow.
     *
     * @return int
     */
    int getContentScrollHeightJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].scrollHeight")).intValue();
    }

    /**
     * Get the width of an element's content, including content not visible on the screen due to overflow.
     *
     * @return int
     */
    int getContentScrollWidthJs() {
        initialWebElementIfNeeded();
        return ((Long) executeScript("return arguments[0].scrollWidth")).intValue();
    }

    /**
     * Check if the element's content scrolled to the bottom
     */
    public boolean isElementContentScrolledToBottom() {
        return getContentVerticalScrollPositionJs() >= getContentScrollHeightJs() - getClientHeightJs();
    }

    /**
     * Check if the element's content scrolled to the right
     */
    public boolean isElementContentScrolledToRight() {
        return getContentHorizontalScrollPositionJs() >= getContentScrollWidthJs() - getClientWidthJs();
    }
}
