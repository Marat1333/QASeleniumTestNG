package com.leroy.core.web_elements.android;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.models.CardWidgetData;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class AndroidScrollView<T extends CardWidgetData> extends BaseWidget {

    AndroidDriver<MobileElement> androidDriver;

    // -------------------- CONSTRUCTORS ---------------------------------//

    public AndroidScrollView(WebDriver driver, CustomLocator locator) {
        this(driver, locator, ".//android.widget.TextView", null);
    }

    public AndroidScrollView(WebDriver driver, By by) {
        this(driver, by, ".//android.widget.TextView", null);
    }

    public AndroidScrollView(WebDriver driver, By by, String eachRowXpath,
                             Class<? extends CardWidget<T>> clazz) {
        this(driver, new CustomLocator(by), eachRowXpath, clazz);
    }

    public AndroidScrollView(WebDriver driver, CustomLocator locator, String eachRowXpath,
                             Class<? extends CardWidget<T>> clazz) {
        super(driver, locator);
        if (clazz == null)
            this.rowWidgetClass = TextViewWidget.class;
        else
            this.rowWidgetClass = clazz;
        this.eachRowXpath = eachRowXpath;
        androidDriver = (AndroidDriver) driver;
    }

    // Constants

    private final int MAX_SCROLL_COUNT = 10;
    public static final String TYPICAL_XPATH = "//android.widget.ScrollView";
    public static final By TYPICAL_LOCATOR = By.xpath("//android.widget.ScrollView");

    // Properties

    // Если будет работать лучше, то надо подумать, как передавать Selector для самого скролла внутрь
    private boolean useUiSelectors = true;
    private Integer swipeDeadZonePercentage = 15;

    public void setUseUiSelectors(boolean useUiSelectors) {
        this.useUiSelectors = useUiSelectors;
    }

    public void setSwipeDeadZonePercentage(int swipeDeadZonePercentage) {
        this.swipeDeadZonePercentage = swipeDeadZonePercentage;
    }

    private Class<? extends BaseWidget> rowWidgetClass;
    private String eachRowXpath;

    // ----- Others -------
    private List<T> tmpCardDataList;
    private CardWidget<T> tmpWidget;

    @AppFindBy(xpath = "//android.widget.ProgressBar", cacheLookup = false, metaName = "Progress bar")
    private Element progressBar;

    private enum Direction {
        UP, DOWN;
    }

    private static class SearchContext {
        Element findElement;
        String findText;
        String findTextShouldNotContainsIt;
    }

    // Helper methods

    private void executeUIAutomatorScript(String script) {
        setImplicitWait(0);
        try {
            androidDriver.findElementByAndroidUIAutomator(script);
        } catch (NoSuchElementException err) {
            // This error is ok.
            // if you know how to execute UIAutomator script with different way, so update this code
        }
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
    }

    // Public methods

    /**
     * Ищет виджет с текстом {value} и возвращает ссылку на сам widget (в общем виде - CardWidget)
     */
    public CardWidget<T> searchForWidgetByText(String containsText, boolean scrollUpBefore) {
        SearchContext searchContext = new SearchContext();
        searchContext.findText = containsText;
        if (scrollUpBefore)
            scrollUp();
        scrollAndGrabData(searchContext, MAX_SCROLL_COUNT, null, Direction.DOWN);
        return tmpWidget;
    }

    public CardWidget<T> searchForWidgetByText(String containsText) {
        return searchForWidgetByText(containsText, false);
    }

    /**
     * Get data object by index. If necessary, it scroll to this object
     */
    public T getDataObj(int index, boolean scrollUpBefore) {
        if (scrollUpBefore)
            scrollUp();
        scrollAndGrabData(null, null, index + 1, Direction.DOWN);
        if (tmpCardDataList.size() <= index) {
            throw new IndexOutOfBoundsException(
                    String.format("Data list size is %s. Index is %s", tmpCardDataList.size(), index));
        }
        return tmpCardDataList.get(index);
    }

    public T getDataObj(int index) {
        return getDataObj(index, false);
    }

    /**
     * Scroll down to the end and get all data as ArrayList
     */
    public List<T> getFullDataList(int maxEntityCount, boolean scrollUpBefore) {
        if (scrollUpBefore)
            scrollUp();
        scrollAndGrabData(null, null, maxEntityCount, Direction.DOWN);
        return new ArrayList<>(tmpCardDataList);
    }

    public List<T> getFullDataList(int maxEntityCount) {
        return getFullDataList(maxEntityCount, false);
    }

    public List<T> getFullDataList() {
        return getFullDataList(30);
    }

    public List<String> getFullDataAsStringList() {
        return getFullDataList().stream().map(T::toString).collect(Collectors.toList());
    }

    /**
     * Return count of the rows inside the ScrollView
     */
    public int getRowCount(boolean scrollUpBefore) {
        if (scrollUpBefore)
            scrollUp();
        return getFullDataList(100).size();
    }

    public int getRowCount() {
        return getRowCount(false);
    }

    private void addNonRepeatingText(List<T> existedList, List<T> newList) {
        int iMatchCount = 0;
        for (T data : existedList) {
            if (iMatchCount >= newList.size())
                break;
            if (data.equals(newList.get(iMatchCount))) {
                iMatchCount++;
            } else {
                iMatchCount = 0;
            }
        }

        for (int i = iMatchCount; i < newList.size(); i++) {
            existedList.add(newList.get(i));
        }
    }

    private void simpleScroll(Direction direction) {
        if (useUiSelectors) {
            String _method = direction.equals(Direction.DOWN) ? "scrollForward" : "scrollBackward";
            StringBuilder runStr = new StringBuilder();
            runStr.append(String.format("new UiScrollable(%s)",
                    XpathUtil.convertXpathToUISelector(getXpath()).toString()));
            if (swipeDeadZonePercentage != null) {
                runStr.append(String.format(".setSwipeDeadZonePercentage(%s)",
                        swipeDeadZonePercentage / 100.0));
            }
            runStr.append(".").append(_method).append("();");
            executeUIAutomatorScript(runStr.toString());
        } else {
            //Try to change bottomY k
            Point _location = getLocation();
            Dimension _size = getSize();
            int x = _location.getX() + _size.getWidth() / 2;
            int bottomY = _location.getY() + _size.getHeight() - (int) Math.round(_size.getHeight() * 0.5);
            int topY = _location.getY() + (int) Math.round(_size.getHeight() * 0.05);

            boolean isDirectionDown = direction.equals(Direction.DOWN);
            new TouchAction<>(androidDriver)
                    .press(point(x, isDirectionDown ? bottomY : topY))
                    .waitAction(waitOptions(ofMillis(1000)))
                    .moveTo(point(x, isDirectionDown ? topY : bottomY))
                    .release().perform();
        }
    }

    /**
     * Scroll to the specific text
     *
     * @param searchContext  - params which should be found
     * @param maxScrollCount - limit of scroll count
     * @param direction      - up or down
     * @return this
     */
    private AndroidScrollView<T> scrollAndGrabData(
            SearchContext searchContext, Integer maxScrollCount,
            Integer maxEntityCount, Direction direction) {
        initialWebElementIfNeeded();
        tmpCardDataList = new ArrayList<>();
        String prevPageSource = null;
        int i = 0;
        while (true) {
            ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(eachRowXpath, rowWidgetClass);
            List<T> currentVisibleDataList = new ArrayList<>();
            String pageSource = getPageSource();
            if (searchContext != null && searchContext.findElement != null) {
                if (searchContext.findElement.isVisible(pageSource))
                    break;
            }
            boolean textFound = false;
            for (CardWidget<T> widget : cardWidgetList) {
                if (widget.isFullyVisible(pageSource)) {
                    T data = widget.collectDataFromPage(pageSource);
                    currentVisibleDataList.add(data);
                    if (searchContext != null && searchContext.findText != null &&
                            data.toString().contains(searchContext.findText) &&
                            (searchContext.findTextShouldNotContainsIt == null ||
                                    !data.toString().contains(searchContext.findTextShouldNotContainsIt))) {
                        tmpWidget = widget;
                        textFound = true;
                        break;
                    }
                }
            }
            addNonRepeatingText(tmpCardDataList, currentVisibleDataList);
            if (textFound)
                return this;
            if (maxScrollCount != null && i >= maxScrollCount)
                break;
            if (maxEntityCount != null && tmpCardDataList.size() >= maxEntityCount) {
                while (tmpCardDataList.size() > maxEntityCount) {
                    tmpCardDataList.remove(tmpCardDataList.size() - 1);
                }
                break;
            }
            if (pageSource.equals(prevPageSource))
                break;
            prevPageSource = pageSource;
            simpleScroll(direction);
            progressBar.waitForInvisibility();
            Log.debug("<-- Scroll down #" + (i + 1) + "-->");
            i++;
        }
        return this;
    }

    /**
     * Scroll down to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView<T> scrollDownToText(String findText, int maxScrollCount) {
        SearchContext searchContext = new SearchContext();
        searchContext.findText = findText;
        return scrollAndGrabData(searchContext, maxScrollCount, null, Direction.DOWN);
    }

    /**
     * Scroll up to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView<T> scrollUpToText(String findText, int maxScrollCount) {
        SearchContext searchContext = new SearchContext();
        searchContext.findText = findText;
        return scrollAndGrabData(searchContext, maxScrollCount, null, Direction.UP);
    }

    public AndroidScrollView<T> scrollDownToText(String findText) {
        return scrollDownToText(findText, MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollDownToElement(Element element) {
        SearchContext searchContext = new SearchContext();
        searchContext.findElement = element;
        return scrollAndGrabData(searchContext, MAX_SCROLL_COUNT, null, Direction.DOWN);
    }

    public AndroidScrollView<T> scrollUpToElement(Element element) {
        SearchContext searchContext = new SearchContext();
        searchContext.findElement = element;
        return scrollAndGrabData(searchContext, MAX_SCROLL_COUNT, null, Direction.UP);
    }

    public AndroidScrollView<T> scrollDown() {
        simpleScroll(Direction.DOWN);
        return this;
    }

    public AndroidScrollView<T> scrollUp() {
        simpleScroll(Direction.UP);
        return this;
    }

    /**
     * Click the element inside the scroll view
     */
    public void clickElemByIndex(int index) throws Exception {
        // Method can be improved with scrolling if it necessary
        ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(eachRowXpath, rowWidgetClass);
        cardWidgetList.get(index).click();
    }

}
