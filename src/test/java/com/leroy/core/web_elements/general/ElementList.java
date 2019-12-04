package com.leroy.core.web_elements.general;

import com.leroy.constants.Fonts;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ElementList<E extends Element> extends BaseElement implements Iterable<E> {

    private List<E> elementList;
    protected List<WebElement> weList;
    private Class<? extends Element> elementClass;

    protected Class<? extends Element> getElementClass() {
        return elementClass;
    }

    protected void setElementClass(Class<? extends Element> elementClass) {
        this.elementClass = elementClass;
    }

    // Constructors
    public ElementList(WebDriver driver, By locator) {
        super(driver, new CustomLocator(locator));
        this.elementClass = Element.class;
    }

    public ElementList(WebDriver driver, By locator, Class<? extends Element> elementClass) {
        this.driver = driver;
        this.locator = new CustomLocator(locator);
        this.elementClass = elementClass;
        initElements(this.locator);
    }

    public ElementList(WebDriver driver, List<By> locators) throws Exception {
        this.driver = driver;
        weList = new ArrayList<>();
        for (By locator : locators) {
            weList.add(findElement(locator));
        }
        this.elementClass = Element.class;
        initElementList(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
    }

    /**
     * Deprecated constructor
     *
     * @param driver
     * @param weList
     * @param elementClass deprecated?? Element list initializing without a locator is inefficient. You should use another constructor
     */
    //TODO: we have to remove this constructor. Element list initializing without a locator is inefficient
    public ElementList(WebDriver driver, List<WebElement> weList, Class<? extends Element> elementClass) {
        this.driver = driver;
        this.weList = weList;
        this.elementClass = elementClass;
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> it = new Iterator<E>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                try {
                    return currentIndex < getCount();
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
        return it;
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
                for (int i = 0; i < weList.size(); i++)
                    elementList.add((E) getElementClass().getConstructor(WebDriver.class, WebElement.class, By.class).
                            newInstance(driver, weList.get(i), By.xpath(XpathUtil.getXpathByIndex(stringXPath, i))));
            } else
                for (WebElement we : weList)
                    elementList.add((E) getElementClass().getConstructor(WebDriver.class, WebElement.class).
                            newInstance(driver, we));
        } catch (InvocationTargetException e) {
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
            if (elementList.size() == 0 || ((Element) elementList.get(0)).isStaleReference()) {
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
    public int getCount() throws Exception {
        if (locator != null) {
            String xpath = getXpath();
            By by;
            if (xpath.contains("%s"))
                by = By.xpath(xpath.replaceAll("%s", ""));
            else
                by = locator.getBy();
            weList = findElements(by, 0);
        }
        return weList.size();
    }

    /**
     * Wait for count of elements equals specific number
     *
     * @param expectedCount
     */
    public void waitForElementCountEquals(int expectedCount) {
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
     * Wait for count of elements is above or equals to the specified number
     *
     * @param expectedCount
     */
    public boolean waitForElementCountEqualsOrAbove(int expectedCount) {
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

    /**
     * Wait for count of visible elements equals specific number
     *
     * @param expectedCount
     */
    public void waitForVisibleElementCountEquals(int expectedCount) {
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
        for (Element we : elementList) {
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
        for (Element we : elementList)
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
        for (Element we : elementList) {
            if (we.isVisible()) {
                if (!checkSvg || we.findChildElement(By.xpath("./ancestor::*[name()='svg']")).isDisplayed()) {
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
        for (Element we : elementList) {
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
     * Get visible elements
     *
     * @return ElementList
     */
    public ElementList<E> getVisibleElements() throws Exception {
        initWebElementListIfNeeded();
        ElementList<E> newList = new ElementList<>(driver, new ArrayList<>(), getElementClass());
        for (E element : elementList) {
            if (element.isVisible())
                newList.add(element);
        }
        //ElementList<E> newList = new ElementList<>(driver, this.weList, this.elementClass);
        //newList.elementList = this.elementList.stream().filter(c -> c.isVisible()).collect(Collectors.toList());
        return newList;
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
     * Get Border Color
     *
     * @return HashSet
     */
    public HashSet<Color> getBorderColorHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<Color> hS = new HashSet<>();
        for (Element we : elementList) {
            hS.add(we.getBorderColor());
        }
        return hS;
    }

    /**
     * Get Border Color
     *
     * @return HashSet
     */
    public HashSet<Color> getColorHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<Color> hS = new HashSet<>();
        for (Element we : elementList) {
            hS.add(we.getColor());
        }
        return hS;
    }

    /**
     * Get rectangle of element by index
     *
     * @param idx
     * @return Rectangle
     */
    public Rectangle getRectangleByIndex(int idx) throws Exception {
        initWebElementListIfNeeded();
        return elementList.get(idx).getRectangle();
    }

    /**
     * Gets web element attribute by index.
     *
     * @param index
     * @param attribute
     * @return String
     * @throws Exception
     */
    public String getAttribute(int index, String attribute) throws Exception {
        initWebElementListIfNeeded();
        if (index >= elementList.size())
            return null;
        return elementList.get(index).getAttribute(attribute);
    }

    /**
     * Gets web element CSS value by index.
     *
     * @param index
     * @param cssValue
     * @return String
     * @throws Exception
     */
    public String getCSSValue(int index, String cssValue) throws Exception {
        initWebElementListIfNeeded();
        if (index >= elementList.size())
            return null;
        return elementList.get(index).getCssValue(cssValue);
    }

    /**
     * Check web element visibility by index
     *
     * @param index
     * @return boolean
     */
    public boolean isVisible(int index) throws Exception {
        initWebElementListIfNeeded();
        if (index >= elementList.size())
            return false;
        return elementList.get(index).isVisible();
    }

    /**
     * Check rotate angles of all web elements in the list
     *
     * @param angles
     * @return boolean
     */
    public boolean isAllRotateAngles(String angles) throws Exception {
        initWebElementListIfNeeded();
        if (elementList.size() < 1) return false;
        for (Element we : elementList) {
            String str =
                    we.getAttribute("transform").substring(we.getAttribute("transform").indexOf("rotate("));
            str = str.replace("rotate(", "").replace(")", "");
            String[] rotates = str.split(" ");
            if (!rotates[0].equals(angles))
                return false;
        }
        return true;
    }

    /**
     * Gets web element's attributes list. Web elements are defined by the locator.
     *
     * @param attribute
     * @return List<String>
     * @throws Exception
     */
    public List<String> getAttributeList(String attribute) throws Exception {
        initWebElementListIfNeeded();
        List<String> result = new ArrayList<>();
        for (Element we : elementList) {
            result.add(we.getAttribute(attribute));
        }
        return result;
    }

    /**
     * Get list with locations of all grid lines
     */
    public List<Point> getLocationList() throws Exception {
        initWebElementListIfNeeded();
        List<Point> list = new ArrayList<>();
        for (Element el : elementList) {
            list.add(el.getLocation());
        }
        return list;
    }

    /**
     * Get rectangles of elements list
     *
     * @return <Rectangle>
     */
    public List<Rectangle> getRectangleList() throws Exception {
        List<Rectangle> listRect = new ArrayList<>();
        for (int i = 0; i < getCount(); i++)
            listRect.add(getRectangleByIndex(i));
        return listRect;
    }

    /**
     * Get rectangles of elements using javascript
     *
     * @return List<Rectangle>
     */
    public List<Rectangle> getRectangleListJs() throws Exception {
        initWebElementListIfNeeded();
        List<Rectangle> listRect = new ArrayList<>();
        for (int i = 0; i < getCount(); i++)
            listRect.add(elementList.get(i).getRectangleJs());
        return listRect;
    }

    /**
     * Get content Text of all web elements in the list
     *
     * @param selfText - Get Text without including text of child elements
     * @return List<String>
     */
    public ArrayList<String> getTextList(boolean selfText) throws Exception {
        initWebElementListIfNeeded();
        ArrayList<String> text = new ArrayList<>();
        for (E we : getElementList()) {
            text.add(we.getText(selfText));
        }
        return text;
    }

    public List<String> getTextList() throws Exception {
        try {
            return getTextList(false);
        } catch (StaleElementReferenceException e) {
            return getTextList(false);
        }
    }

    /**
     * Get text content of all visible web elements in the list
     *
     * @return List<String>
     */
    public List<String> getVisibleTextList() throws Exception {
        initWebElementListIfNeeded();
        List<String> text = new ArrayList<>();
        for (E we : getElementList()) {
            if (we.isVisible())
                text.add(we.getText());
        }
        return text;
    }

    public HashSet<String> getFontSizeHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<String> hS = new HashSet<>();
        for (E we : getElementList()) {
            String size = we.getFontSize();
            hS.add(size);
        }
        return hS;
    }

    public HashSet<Fonts> getFontWeightHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<Fonts> hS = new HashSet<>();
        for (E we : getElementList()) {
            Fonts size = we.getFontWeight();
            hS.add(size);
        }
        return hS;
    }

    public HashSet<String> getFontFamilyHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<String> hS = new HashSet<>();
        for (E we : getElementList()) {
            String size = we.getFontFamily();
            hS.add(size);
        }
        return hS;
    }

    public HashSet<String> getFontStyleHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<String> hS = new HashSet<>();
        for (E we : getElementList()) {
            String size = we.getFontStyle();
            hS.add(size);
        }
        return hS;
    }

    public HashSet<Integer> getDecimalPlacesHashSet() throws Exception {
        initWebElementListIfNeeded();
        HashSet<Integer> hS = new HashSet<>();
        for (E we : getElementList()) {
            String text = we.getText();
            if (text.contains(".")) {
                int quantityZero = text.split("\\.")[1].length();
                hS.add(quantityZero);
                return hS;
            }
            hS.add(0);
        }
        return hS;
    }

    /**
     * Get visible elements
     *
     * @return ArrayList<E>
     * @throws Exception
     */
    public ArrayList<E> getVisibleElementsAsList() throws Exception {
        return getVisibleElementsAsList(3);
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
