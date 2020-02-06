package com.leroy.core.web_elements.android;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
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

    private final int MAX_SCROLL_COUNT = 10;

    @AppFindBy(xpath = "//android.widget.ProgressBar", cacheLookup = false, metaName = "Progress bar")
    private Element progressBar;

    public static final String TYPICAL_XPATH = "//android.widget.ScrollView";
    public static final By TYPICAL_LOCATOR = By.xpath("//android.widget.ScrollView");
    private Class<? extends BaseWidget> rowWidgetClass;
    private String oneRowXpath;

    AndroidDriver<MobileElement> androidDriver;

    private List<T> tmpCardDataList;
    private CardWidget<T> tmpWidget;

    public AndroidScrollView(WebDriver driver, CustomLocator locator) {
        this(driver, locator, ".//android.widget.TextView", null);
    }

    public AndroidScrollView(WebDriver driver, By by, String oneRowXpath,
                             Class<? extends CardWidget<T>> clazz) {
        this(driver, new CustomLocator(by), oneRowXpath, clazz);
    }

    public AndroidScrollView(WebDriver driver, CustomLocator locator, String oneRowXpath,
                             Class<? extends CardWidget<T>> clazz) {
        super(driver, locator);
        if (clazz == null)
            this.rowWidgetClass = TextViewWidget.class;
        else
            this.rowWidgetClass = clazz;
        this.oneRowXpath = oneRowXpath;
        androidDriver = (AndroidDriver) driver;
    }

    /**
     * Ищет виджет с текстом {value} и возвращает ссылку на сам widget (в общем виде - CardWidget)
     */
    public CardWidget<T> searchForWidgetByText(String containsText, String shouldNotContainsThisText) {
        SearchContext searchContext = new SearchContext();
        searchContext.findText = containsText;
        searchContext.findTextShouldNotContainsIt = shouldNotContainsThisText;
        scrollTo(searchContext, MAX_SCROLL_COUNT, null, "down");
        return tmpWidget;
    }

    public CardWidget<T> searchForWidgetByText(String containsText) {
        return searchForWidgetByText(containsText, null);
    }

    /**
     * Get data object by index. If necessary, it scroll to this object
     */
    public T getDataObj(int index) {
        scrollTo(null, null, index + 1, "down");
        return tmpCardDataList.get(tmpCardDataList.size() - 1);
    }

    /**
     * Scroll down to the end and get all data as ArrayList
     */
    public List<T> getFullDataList(int maxEntityCount) {
        scrollTo(null, null, maxEntityCount, "down");
        return new ArrayList<>(tmpCardDataList);
    }

    public List<T> getFullDataList() {
        return getFullDataList(30);
    }

    public List<String> getFullDataAsStringList() {
        return getFullDataList().stream().map(T::toString).collect(Collectors.toList());
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

    private void simpleScroll(WebElement startElement, WebElement endElement) {
        int startX = startElement.getLocation().getX() + (startElement.getSize().getWidth() / 2);
        int startY = startElement.getLocation().getY() + (startElement.getSize().getHeight() / 2);

        int endX = endElement.getLocation().getX() + (endElement.getSize().getWidth() / 2);
        int endY = endElement.getLocation().getY() + (endElement.getSize().getHeight() / 2);

        new TouchAction<>(androidDriver)
                .press(point(startX, startY))
                .waitAction(waitOptions(ofMillis(500)))
                .moveTo(point(endX, endY))
                .release().perform();
    }

    private void simpleScroll(String direction) {
        Point _location = getLocation();
        Dimension _size = getSize();
        int x = _location.getX() + _size.getWidth() / 2;
        int bottomY = _location.getY() + _size.getHeight() - (int) Math.round(_size.getHeight() * 0.2);
        int topY = _location.getY() + (int) Math.round(_size.getHeight() * 0.2);

        boolean isDirectionDown = direction.equals("down");
        new TouchAction<>(androidDriver)
                .press(point(x, isDirectionDown ? bottomY : topY))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(x, isDirectionDown ? topY : bottomY))
                .release().perform();
    }

    /**
     * Scroll to the specific text
     *
     * @param searchContext  - params which should be found
     * @param maxScrollCount - limit of scroll count
     * @param direction      - up or down
     * @return this
     */
    private AndroidScrollView<T> scrollTo(
            SearchContext searchContext, Integer maxScrollCount, Integer maxEntityCount, String direction) {
        initialWebElementIfNeeded();
        tmpCardDataList = new ArrayList<>();
        List<T> prevDataList = null;
        int i = 0;
        while (true) {
            ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(oneRowXpath, rowWidgetClass);
            List<T> currentVisibleDataList = new ArrayList<>();
            String pageSource = getPageSource();
            if (searchContext.findElement != null) {
                if (searchContext.findElement.isVisible(pageSource))
                    break;
            }
            boolean textFound = false;
            for (CardWidget<T> widget : cardWidgetList) {
                if (widget.isFullyVisible(pageSource)) {
                    T data = widget.collectDataFromPage(pageSource);
                    currentVisibleDataList.add(data);
                    if (searchContext.findText != null && data.toString().contains(searchContext.findText) &&
                            (searchContext.findTextShouldNotContainsIt == null ||
                            !data.toString().contains(searchContext.findTextShouldNotContainsIt))) {
                        tmpWidget = widget;
                        textFound = true;
                        break;
                    }
                }
            }
            if (currentVisibleDataList.size() == 0) {
                Log.warn("Couldn't find elements during scroll");
                break;
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
            simpleScroll(direction);
            progressBar.waitForInvisibility();
            Log.debug("<-- Scroll down #" + (i + 1) + "-->");
            if (currentVisibleDataList.equals(prevDataList))
                break;
            prevDataList = new ArrayList<>(currentVisibleDataList);
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
        return scrollTo(searchContext, maxScrollCount, null, "down");
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
        return scrollTo(searchContext, maxScrollCount, null, "up");
    }

    public AndroidScrollView<T> scrollDownToText(String findText) {
        return scrollDownToText(findText, MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollDownToElement(Element element) {
        SearchContext searchContext = new SearchContext();
        searchContext.findElement = element;
        return scrollTo(searchContext, MAX_SCROLL_COUNT, null, "down");
    }

    public AndroidScrollView<T> scrollUpToElement(Element element) {
        SearchContext searchContext = new SearchContext();
        searchContext.findElement = element;
        return scrollTo(searchContext, MAX_SCROLL_COUNT, null, "up");
    }

    public AndroidScrollView<T> scrollDown() {
        return scrollDown(MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollUp() {
        return scrollUp(MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollDown(int count) {
        if (count == 1)
            simpleScroll("down");
        return scrollDownToText(null, count);
    }

    public AndroidScrollView<T> scrollUp(int count) {
        if (count == 1)
            simpleScroll("up");
        return scrollUpToText(null, count);
    }

    /**
     * Click the element inside the scroll view
     */
    public void clickElemByIndex(int index) throws Exception {
        // Method can be improved with scrolling if it necessary
        ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(oneRowXpath, rowWidgetClass);
        cardWidgetList.get(index).click();
    }

    // ------------ SEARCH CONTEXT ---------------- //

    private static class SearchContext {
        Element findElement;
        String findText;
        String findTextShouldNotContainsIt;
    }

}
