package com.leroy.core.web_elements.general;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ElementList<E extends BaseWidget> extends BaseWrapper implements Iterable<E> {

    private List<E> elementList;
    protected List<WebElement> weList;
    private Class<? extends BaseWidget> elementClass;

    protected Class<? extends BaseWidget> getElementClass() {
        return elementClass;
    }

    protected void setElementClass(Class<? extends BaseWidget> elementClass) {
        this.elementClass = elementClass;
    }

    // Constructors
    public ElementList(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
        this.elementClass = Element.class;
    }

    public ElementList(WebDriver driver, CustomLocator locator, Class<? extends BaseWidget> elementClass) {
        this(driver, locator);
        this.elementClass = elementClass;
        initElements(this.locator);
    }

    @Override
    public Iterator<E> iterator() {
        int i = 0;
        try {
            i = getCount();
            if (i > 0) {
                initElementList(timeout);
            }
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
        int elementCount = i;
        return new Iterator<E>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                try {
                    return currentIndex < elementCount;
                } catch (Exception err) {
                    Log.error(err.getMessage());
                    return false;
                }
            }

            @Override
            public E next() {
                try {
                    return get(currentIndex++);
                } catch (Exception err) {
                    Log.error(err.getMessage());
                    return null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // Protected and Private methods

    /**
     * Initializing the list of web elements
     */
    protected void initElementList(int timeout) throws Exception {
        initElementList(timeout, 3);
    }

    /**
     * Initializing the list of web elements
     *
     * @param timeout
     * @param attemptsNumber
     * @throws Exception
     */
    private void initElementList(int timeout, int attemptsNumber) throws Exception {
        if (locator != null) {
            weList = findElements(locator, timeout);
        }
        elementList = new ArrayList<>();
        try {
            if (locator != null) {
                String stringXPath = getXpath();
                for (int i = 0; i < weList.size(); i++) {
                    CustomLocator elementLocator = new CustomLocator(
                            By.xpath(XpathUtil.getXpathByIndex(stringXPath, i)));
                    elementLocator.setCacheLookup(true);
                    E oneElem = (E) getElementClass().getConstructor(WebDriver.class, CustomLocator.class).
                            newInstance(driver, elementLocator);
                    oneElem.setWebElement(weList.get(i));
                    elementList.add(oneElem);
                }
            } else
                for (WebElement we : weList)
                    elementList.add((E) getElementClass().getConstructor(WebDriver.class, WebElement.class).
                            newInstance(driver, we));
        } catch (InvocationTargetException e) {
            Log.error(e.getMessage());
            if (attemptsNumber > 0) {
                clearElementList();
                initElementList(timeout, attemptsNumber - 1);
            }
        }
    }

    protected void initWebElementListIfNeeded() throws Exception {
        initWebElementListIfNeeded(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
    }

    protected void initWebElementListIfNeeded(int timeout) throws Exception {
        if (elementList == null)
            initElementList(timeout);
        else {
            if (elementList.size() == 0 || elementList.get(0).isStaleReference()) {
                initElementList(0);
            }
        }
    }

    /**
     * Initializes the web Elements again
     *
     * @throws Exception
     */
    public void refresh() throws Exception {
        initElementList(0);
    }

    protected void clearElementList() {
        elementList.clear();
    }

    //public methods

    public E get(int index) throws Exception {
        initWebElementListIfNeeded();
        return elementList.get(index);
    }

    /**
     * Returns the elementList as is (without initialization)
     *
     * @return List<E>
     * @throws Exception
     */
    protected List<E> getElementList() throws Exception {
        return elementList;
    }

    protected void setElementList(List<E> list) {
        elementList = list;
    }

    public E last() throws Exception {
        initWebElementListIfNeeded();
        return elementList.get(elementList.size() - 1);
    }

    /**
     * Add element in ElementList
     *
     * @param el
     */
    public void add(E el) {
        if (elementList == null) {
            elementList = new ArrayList<>();
        }
        elementList.add(el);
        weList.add(((Element) el).webElement);
    }

    /**
     * Get count of web elements
     *
     * @return int
     */
    public int getCount() {
        if (locator != null) {
            String xpath = getXpath();
            By by;
            if (xpath.contains("%s"))
                by = By.xpath(xpath.replaceAll("%s", ""));
            else
                by = By.xpath(xpath);
            weList = findElements(by, 0);
        }
        return weList.size();
    }

    /**
     * Wait for count of elements equals specific number
     *
     * @param expectedCount
     */
    public void waitUntilElementCountEquals(int expectedCount) {
        try {
            new WebDriverWait(this.driver, timeout).until((driver) -> {
                try {
                    return getCount() == expectedCount;
                } catch (Exception var4) {
                    return false;
                }
            });
        } catch (TimeoutException err) {
            Log.warn(String.format("Expected condition failed: waitForElementCountEquals (tried for %d second(s))", timeout));
        }
    }

    /**
     * Wait until at least one element appears
     */
    public boolean waitUntilAtLeastOneElementIsPresent(int timeout) {
        return waitUntilElementCountEqualsOrAbove(1, timeout);
    }

    public boolean waitUntilAtLeastOneElementIsPresent() {
        return waitUntilElementCountEqualsOrAbove(1);
    }

    /**
     * Wait for count of elements is above or equals to the specified number
     *
     * @param expectedCount
     */
    public boolean waitUntilElementCountEqualsOrAbove(int expectedCount, int timeout) {
        try {
            new WebDriverWait(this.driver, timeout).until((driver) -> {
                try {
                    return getCount() >= expectedCount;
                } catch (Exception e) {
                    return false;
                }
            });
            return true;
        } catch (TimeoutException err) {
            Log.warn(String.format(
                    "Expected condition failed: waitForElementCountEqualsOrAbove (tried for %d second(s))", timeout));
            return false;
        }
    }

    public boolean waitUntilElementCountEqualsOrAbove(int expectedCount) {
        return waitUntilElementCountEqualsOrAbove(expectedCount, timeout);
    }

    /**
     * Wait for count of visible elements equals specific number
     *
     * @param expectedCount
     */
    public void waitUntilVisibleElementCountEquals(int expectedCount) {
        try {
            new WebDriverWait(this.driver, timeout).until((driver) -> {
                try {
                    return getCountOfVisibleElements() == expectedCount;
                } catch (Exception var4) {
                    return false;
                }
            });
        } catch (TimeoutException err) {
            Log.warn(String.format("Expected condition failed: waitForVisibleElementCountEquals (tried for %d second(s))", timeout));
        }
    }

    /**
     * Confirm that at least one Element is present and all of them are visible
     */
    public boolean confirmVisibility() throws Exception {
        initWebElementListIfNeeded();
        if (elementList.size() < 1) {
            Log.error("There are none elements");
            return false;
        }
        int i = 0;
        for (E we : elementList) {
            if (!we.isVisible()) {
                Log.error("Element#" + i + " is not visible");
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * Confirm that at least one Element is present and is visible
     */
    public boolean confirmAtLeastOneElementIsVisible() throws Exception {
        initWebElementListIfNeeded();
        if (elementList.size() < 1) {
            Log.error("There are none elements");
            return false;
        }
        for (E we : elementList)
            if (we.isVisible())
                return true;
        return false;
    }

    /**
     * Confirm that none elements are present or all of them are invisible
     */
    public boolean confirmInvisibility() throws Exception {
        return confirmInvisibility(false);
    }

    /**
     * Confirm that none elements are present or all of them are invisible
     *
     * @param checkSvg - if true the method works very slowly. Use only if you really need.
     * @return
     * @throws Exception
     */
    public boolean confirmInvisibility(boolean checkSvg) throws Exception {
        initWebElementListIfNeeded(0);
        int i = 0;
        for (E we : elementList) {
            if (we.isVisible()) {
                if (!checkSvg || we.findChildWebElement(By.xpath("./ancestor::*[name()='svg']")).isDisplayed()) {
                    Log.error("Element#" + i + " is visible");
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    /**
     * Get count of visible elements
     *
     * @return int
     */
    public int getCountOfVisibleElements() throws Exception {
        return getCountOfVisibleElements(2);
    }

    private int getCountOfVisibleElements(int attemptNumber) throws Exception {
        initElementList(0);
        int count = 0;
        for (E we : elementList) {
            if (!we.isPresent()) {
                if (attemptNumber > 0)
                    return getCountOfVisibleElements(attemptNumber - 1);
                else throw new RuntimeException("getCountOfVisibleElements() failed " +
                        "because ElementList has been changed. Xpath: " + getXpath());
            }
            if (we.isVisible())
                count++;
        }
        return count;
    }

    /**
     * Gets web element's CSS attributes list. Web elements are defined by the locator.
     *
     * @param cssAttribute
     * @return HashSet<String>
     * @throws Exception
     */
    public HashSet<String> getHashSetElementCssAttribute(String cssAttribute) throws Exception {
        HashSet<String> result = new HashSet<>();
        List<WebElement> weList = findElements(locator);
        for (WebElement we : weList) {
            result.add(we.getCssValue(cssAttribute));
        }
        return result;
    }

    /**
     * Gets web element's attributes list. Web elements are defined by the locator.
     *
     * @param attribute
     * @return HashSet<String>
     * @throws Exception
     */
    public HashSet<String> getHashSetElementAttribute(String attribute) throws Exception {
        HashSet<String> result = new HashSet<>();
        List<WebElement> weList = findElements(locator);
        for (WebElement we : weList) {
            result.add(we.getAttribute(attribute));
        }
        return result;
    }

    /**
     * Get content Text of all web elements in the list
     */
    public List<String> getTextList(String pageSource) throws Exception {
        initWebElementListIfNeeded();
        if (pageSource == null)
            pageSource = getPageSource();
        ArrayList<String> text = new ArrayList<>();
        for (E we : getElementList()) {
            text.add(((Element) we).getText(pageSource));
        }
        return text;
    }

    public List<String> getTextList() throws Exception {
        try {
            return getTextList(null);
        } catch (StaleElementReferenceException e) {
            return getTextList(null);
        }
    }

    /**
     * Wait for text list is changed
     *
     * @param contentBefore
     * @return
     */
    public boolean waitUntilTextListIsChanged(List<String> contentBefore) {
        try {
            new WebDriverWait(this.driver, timeout).until(driver -> {
                try {
                    return !getTextList().equals(contentBefore);
                } catch (Exception var4) {
                    return false;
                }
            });
            return true;
        } catch (TimeoutException err) {
            Log.warn(String.format("Expected condition failed: waitForTextListIsChanged (tried for %d second(s))",
                    timeout));
            return false;
        }
    }

    /**
     * Get visible elements
     *
     * @return ArrayList<E>
     * @throws Exception
     */
    public ArrayList<E> getVisibleElementsAsList() throws Exception {
        return getVisibleElementsAsList(1);
    }

    /**
     * Get visible elements
     *
     * @param attemptsNumber - defines additional attempts to getting visible elements
     * @return ArrayList<E>
     */
    private ArrayList<E> getVisibleElementsAsList(int attemptsNumber) throws Exception {
        initWebElementListIfNeeded();
        ArrayList<E> resultList = new ArrayList<>();
        try {
            for (E element : getElementList())
                if (element.isVisible())
                    resultList.add(element);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            if (attemptsNumber > 0) {
                clearElementList();
                resultList.clear();
                resultList = getVisibleElementsAsList(attemptsNumber - 1);
            } else
                throw new RuntimeException("Method: ElementList.getVisibleElementsAsList() - " +
                        "we can't obtain visible elements due an exception. Exception message: " + e.getMessage());
        }
        return resultList;
    }

    /**
     * Initializes the list of elements and returns it
     *
     * @return List<E>
     * @throws Exception
     */
    public List<E> convertToList() throws Exception {
        initWebElementListIfNeeded();
        return getElementList();
    }
}
